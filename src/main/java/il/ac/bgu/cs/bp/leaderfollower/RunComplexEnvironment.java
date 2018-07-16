package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.SingleResourceBProgram;
import static il.ac.bgu.cs.bp.leaderfollower.SourceUtils.readResource;
import java.io.IOException;

/**
 * This class runs the complex environment so we can test that it works.
 * @author michael
 */
public class RunComplexEnvironment {
    
    public static void main(String[] args) throws IOException {
        new RunComplexEnvironment().start();
    }
    
    public void start() throws IOException {
        BProgram model = new SingleResourceBProgram("ControllerLogic.js");
        model.prependSource( readResource("CommonLib.js") );
        model.prependSource("bp.log.setLevel(\"Fine\");");
        model.appendSource( readResource("ComplexSimulatedEnvironment.js") );
        
        BProgramRunner rnr = new BProgramRunner(model);
        rnr.addListener( new PrintBProgramRunnerListener() );
        
        rnr.run();
        
    }
}
