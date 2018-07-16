package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.bpjs.analysis.BProgramStateVisitedStateStore;
import il.ac.bgu.cs.bp.bpjs.analysis.DfsBProgramVerifier;
import il.ac.bgu.cs.bp.bpjs.analysis.Node;
import il.ac.bgu.cs.bp.bpjs.analysis.VerificationResult;
import il.ac.bgu.cs.bp.bpjs.analysis.listeners.BriefPrintDfsVerifierListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.SingleResourceBProgram;
import static il.ac.bgu.cs.bp.leaderfollower.SourceUtils.readResource;
import java.io.PrintStream;

/**
 *
 * @author michael
 */
public class ComplexEnvironmentVerification {
    
    public static void main(String[] args) throws Exception {
        new ComplexEnvironmentVerification().start();
    }
    
    public void start() throws Exception {
        // create the compound model
        BProgram model = new SingleResourceBProgram("ControllerLogic.js");
        model.prependSource( readResource("CommonLib.js") );
        model.appendSource( readResource("ComplexSimulatedEnvironment.js") );
        model.appendSource( readResource("ComplexEnvironmentAssertions.js") );
        
        // Create the verifier
        DfsBProgramVerifier vfr = new DfsBProgramVerifier();
        vfr.setMaxTraceLength(300);
        vfr.setProgressListener( new BriefPrintDfsVerifierListener() );
        vfr.setVisitedNodeStore( new BProgramStateVisitedStateStore(false) );
        
        VerificationResult verificationResult = vfr.verify(model);
        
        System.out.println("States scanned: " + verificationResult.getScannedStatesCount() );
        System.out.println("Time (msec): " + verificationResult.getTimeMillies());
        
        if ( verificationResult.isCounterExampleFound() ) {
            System.out.println("Counter example found. Type: " + verificationResult.getViolationType());
            if ( verificationResult.getFailedAssertion() != null ) {
                System.out.println("Verification message: " + verificationResult.getFailedAssertion().getMessage());
            }
            verificationResult.getCounterExampleTrace().forEach( n->prettyPrintNode(n, System.out) );
            
        } else {
            System.out.println("No counter example found.");
        }
    }
    
    private void prettyPrintNode( Node n, PrintStream out ) {
        out.println( n.getLastEvent() );
        out.println();
        n.getSystemState().getBThreadSnapshots().stream()
            .sorted( (a,b)->a.getName().compareTo(b.getName()) )
            .forEach( btss -> {
                out.printf(" %s: r:%s\tw:%s\tb:%s\n", btss.getName(), btss.getBSyncStatement().getRequest(), btss.getBSyncStatement().getWaitFor(), btss.getBSyncStatement().getBlock());
            });
    }
}
