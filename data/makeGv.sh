#!/bin/bash
echo "Takes run data and created a Graphviz file."
echo "Usage: makeGv datafile"
exec scala "$0" "$@"
!#
import java.nio.file.{Paths,Path,Files}
import java.io._
import java.nio.file._

object App {

  def printTrace( pref:String, moves:Seq[(String,String,String)], out:PrintStream )  {
    val pointData = moves.zipWithIndex.map( pi => (pref+pi._2, pi._1._1, pi._1._2, pi._1._3))
    pointData.foreach( tpl => {
      out.println("%s[pos=\"%s,%s!\"]".format( tpl._1, tpl._2, tpl._3))
    })
    pointData.sliding(2).foreach( tpl => out.println("%s -> %s".format(tpl(0)._1, tpl(1)._1)))
  }

  def start( args:Seq[String] ) = {
    val inputFileName = args(0)
    println("Reading from %s" format inputFileName);

    val raw = io.Source.fromFile(new java.io.File(inputFileName)).getLines
    val justEvents = raw.filter( s => s.startsWith("[BEvent")).toSeq

    Files.write( Paths.get(inputFileName).resolveSibling(inputFileName+"-events.log"),
                 justEvents.mkString("\n").getBytes )

    // [BEvent name:Telemetry(1.0,1.0,1.0,15.0,0.0,14.0)]
    val parsed = justEvents.filter(_.contains("Telemetry"))
                           .map( l => l.split("\\(")(1) )
                           .map( l => l.split("\\)")(0) )
                           .map( l => l.split(",") )
                           .map( a => ((a(0),a(1),a(4)),(a(2),a(3),"")) )
    val rover = parsed.map( _._1 )
    val leader = parsed.map( _._2 )

    val fileName = inputFileName + ".gv"
    val ps = new PrintStream( new FileOutputStream(fileName) );
    ps.println("digraph leaderfollower {")

    printTrace("L", leader, ps)
    ps.println("node [color=blue]")
    ps.println("edge [color=blue]")
    printTrace("R", rover, ps)

    ps.println("edge [color=gray]")
    Range(0,leader.size).foreach( n => ps.println("%s->%s".format("R"+n, "L"+n)))

    ps.println("}")
    ps.close
  }
}

App.start( args )
