#!/bin/bash
echo "Takes run data and created a Graphviz file."
echo "Usage: makeGv datafile"
exec scala "$0" "$@"
!#
/*
Usage: ./adder.scala
*/
import java.nio.file.{Paths,Path,Files}
import java.io._

object App {

  def printTrace( pref:String, moves:Seq[(String,String,String)], out:PrintStream )  {
    val pointData = moves.zipWithIndex.map( pi => (pref+pi._2, pi._1._1, pi._1._2, pi._1._3))
    pointData.foreach( tpl => {
      out.println("%s[pos=\"%s,%s!\"]".format( tpl._1, tpl._2, tpl._3))
    })
    pointData.sliding(2).foreach( tpl => out.println("%s -> %s".format(tpl(0)._1, tpl(1)._1)))
  }

  def start( args:Seq[String] ) = {
    println("Reading from %s" format args(0));

    val raw = io.Source.fromFile(new java.io.File(args(0))).getLines
    val parsed = raw.map( s => s.split("\t") ).map( arr => ((arr(0),arr(1),arr(2)), (arr(3),arr(4),arr(5)))).toSeq
    val rover = parsed.map( _._1 )
    val leader = parsed.map( _._2 )

    val fileName = args(0) + ".gv"
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
