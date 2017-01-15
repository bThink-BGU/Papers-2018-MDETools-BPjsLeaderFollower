/* global bp */
/**
 * Simple "Hello world" program.
 */

bp.registerBThread( "helloBT", function(){
    bsync( {request:bp.Event("Hello,")} );
} );

bp.registerBThread( "worldBT", function(){
    bsync( {request:bp.Event("World!")} );
} );

bp.registerBThread( "arbiter", function(){
    bsync( {waitFor:bp.Event("Hello,"),
              block:bp.Event("World!")} );
} );