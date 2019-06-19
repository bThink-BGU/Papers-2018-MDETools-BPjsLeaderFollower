package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.bpjs.analysis.BThreadSnapshotVisitedStateStore;
import il.ac.bgu.cs.bp.bpjs.analysis.DfsBProgramVerifier;
import il.ac.bgu.cs.bp.bpjs.analysis.DfsTraversalNode;
import il.ac.bgu.cs.bp.bpjs.analysis.VerificationResult;
import il.ac.bgu.cs.bp.bpjs.analysis.listeners.BriefPrintDfsVerifierListener;
import il.ac.bgu.cs.bp.bpjs.analysis.violations.Violation;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.ResourceBProgram;
import java.io.PrintStream;
import static il.ac.bgu.cs.bp.leaderfollower.SourceUtils.*;
/**
 * This class model-checks the rover control b-program.
 * 
 * @author michael
 */
public class SimpleModelChecking {
    
    public static void main(String[] args) throws Exception {
        new SimpleModelChecking().start();
    }
    
    public void start() throws Exception {
        // create the compound model
        BProgram model = new ResourceBProgram("ControllerLogic.js");
        model.prependSource( readResource("CommonLib.js") );
        model.appendSource( readResource("SimpleSimulatedEnvironment.js") );
        model.appendSource( readResource("ModelAssertions.js") );
        
        // Create the verifier
        DfsBProgramVerifier vfr = new DfsBProgramVerifier();
        vfr.setMaxTraceLength(300);
        vfr.setProgressListener( new BriefPrintDfsVerifierListener() );
        vfr.setVisitedNodeStore( new BThreadSnapshotVisitedStateStore() );
        
//        vfr.setDetectDeadlocks(false); // Should go away in next version
        
        VerificationResult verificationResult = vfr.verify(model);
        
        if ( verificationResult.isViolationFound() ) {
            Violation v = verificationResult.getViolation().get();
            // System.out.println("Counter example found. Type: " + v.getViolationType());
            // if ( v.getFailedAssertion() != null ) {
                // System.out.println("Verification message: " + verificationResult.getFailedAssertion().getMessage());
            // }
            
            v.getCounterExampleTrace().forEach( n->prettyPrintNode(n, System.out) );
        } else {
            System.out.println("No counter example found.");
        }
    }
    
    private void prettyPrintNode( DfsTraversalNode n, PrintStream out ) {
        out.println( n.getLastEvent() );
        out.println();
        n.getSystemState().getBThreadSnapshots().stream()
            .sorted( (a,b)->a.getName().compareTo(b.getName()) )
            .forEach( btss -> {
                out.printf(" %s: r:%s\tw:%s\tb:%s\n", btss.getName(), btss.getBSyncStatement().getRequest(), btss.getBSyncStatement().getWaitFor(), btss.getBSyncStatement().getBlock());
            });
    }
    
}
