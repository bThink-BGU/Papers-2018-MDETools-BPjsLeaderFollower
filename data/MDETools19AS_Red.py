from z3 import Bool, Reals, And, Or, is_true, BoolSort, simplify, Ints
from z3 import is_rational_value, Solver, sat, Not, RealVal
from z3 import *
import socket
import time
import threading
import math

leaderSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
roverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

def SplitInData (s):
       s1=s.split(';')
       s2=s1[0].split(',')
       return s2



def getTelem():
    global RoverPx, RoverPy, Compass, LeaderPx, LeaderPy, Dist, DDeg, GDDeg
    time.sleep(0.03)
    print ("started telem")
    roverSocket.sendall(b"player3,GPS()\n")
    rData = repr(roverSocket.recv(1024))
    RoverGPSData = SplitInData(rData)
    RoverPx = float(RoverGPSData[1])
    RoverPy = float(RoverGPSData[2])
    print ("Rover Pos x:", RoverPx)
    print ("Rover Pos y:", RoverPy)
        
    roverSocket.sendall(b"player3,getCompass()\n")
    rData = repr(roverSocket.recv(1024))
    RoverCompassData = SplitInData(rData)
    Compass = float(RoverCompassData[1])
    print ("Compass:", Compass)
        
    roverSocket.sendall(b"ball,GPS()\n")
    lData = repr(roverSocket.recv(1024))
    LeaderGPSData = SplitInData(lData)
    LeaderPx = float(LeaderGPSData[1])
    LeaderPy = float(LeaderGPSData[2])
    print ("Leader Pos x:", LeaderPx)
    print ("Leader Pos y:", LeaderPy)
        
    LeaderDistanceData = pow(((RoverPx-LeaderPx)*(RoverPx-LeaderPx)+(RoverPy-LeaderPy)*(RoverPy-LeaderPy)),(1/2))
    Dist = LeaderDistanceData
    print ("Distance:", Dist)

    LRDeg = math.atan2((LeaderPx - RoverPx), -(LeaderPy - RoverPy))
    LRDeg = (LRDeg / math.pi) * 180
    DDeg = (90 - Compass) - LRDeg

    if (abs(DDeg) >= 360):
        if (DDeg > 0):
            DDeg = DDeg - 360;
        else:
            DDeg = DDeg + 360;

    if (abs(DDeg) > 180):
        if (DDeg > 180):
            DDeg = DDeg - 360
            
        if (DDeg < (-180)):
            DDeg = DDeg + 360
    print ("DDeg:",DDeg)
    print ("#################################")

    GLRDeg = math.atan2((-50 - RoverPx), -(0 - RoverPy))
    GLRDeg = (GLRDeg / math.pi) * 180
    GDDeg = (90 - Compass) - GLRDeg

    if (abs(GDDeg) >= 360):
        if (GDDeg > 0):
            GDDeg = GDDeg - 360;
        else:
            GDDeg = GDDeg + 360;

    if (abs(GDDeg) > 180):
        if (GDDeg > 180):
            GDDeg = GDDeg - 360
            
        if (GDDeg < (-180)):
            GDDeg = GDDeg + 360
    print ("GDDeg:",GDDeg)
    print ("#################################")
    


true = BoolSort().cast(True)
false = BoolSort().cast(False)

reset = Bool('reset')
BInRobot = Bool('BInRobot')
pL, pR, Su = Ints('pL pR Su')



# Initial state
init = Solver()
init.add(pL == 0, pR == 0, Su==0, BInRobot==False)

init.check()
m = init.model()


# B-Threads
global TooFar, TooClose, MaxPower

TooFar=5
TooClose=3.5
MaxPower=100


def bounds():
       yield {'must': And(pL >= -MaxPower, pL <= MaxPower,pR >= -MaxPower, pR<= MaxPower)}


def turnpowers():
       yield {'must': Implies(pL != pR , Or(And(pL==0, pR==40), And(pL==40 , pR==0)))}

       
def forward():
       while True:
              if (Dist>TooClose):
                     if (Dist<TooFar):
                            yield {'may': And(pL==((Dist-TooClose)/(TooFar-TooClose))*MaxPower,
                                              pR==((Dist-TooClose)/(TooFar-TooClose))*MaxPower),
                                   'wait-for': true}
                     else:
                            yield {'may': And(pL==MaxPower, pR==MaxPower),
                                   'wait-for': true}
                     
              else:
                     if (Dist>(2*TooClose-TooFar)):
                            yield {'may': And(pL==((Dist-TooClose)/(TooFar-TooClose))*MaxPower,
                                              pR==((Dist-TooClose)/(TooFar-TooClose))*MaxPower),
                                   'wait-for': true}
                     else:
                            yield {'may': And(pL==-MaxPower,pR==-MaxPower),
                                   'wait-for': true} 

def spinToBall():
       while True:
           if is_false(m[BInRobot]):
              if (abs(DDeg)>10):
                     if (DDeg>0):
                            yield {'may': pL>pR,
                                   'must': pL>pR,
                                   'wait-for': true}
                     else:
                            yield {'may': pR>pL,
                                   'must': pR>pL,
                                   'wait-for': true}
              else:
                     yield {'wait-for': true}
           else:
             yield {'wait-for': true}

def spinToGoal():
       while True:
           if is_true(m[BInRobot]):
              if (abs(GDDeg)>8):
                     if (GDDeg>0):
                            yield {'may': pL>pR,
                                   'must': pL>pR,
                                   'wait-for': true}
                     else:
                            yield {'may': pR>pL,
                                   'must': pR>pL,
                                   'wait-for': true}
              else:
                     yield {'wait-for': true}
           else:
             yield {'wait-for': true}


def getBall():
       while True:
           if is_false(m[BInRobot]):
              if Dist<4.3:
                     yield {'must': And(Su==-100, BInRobot==True),
                            'wait-for': true}
              else:
                     yield {'must': And(Su==-100, BInRobot==False),
                            'wait-for': true}  
           else:
              yield {'wait-for': true}
           


def shootBall():
       while True:
            if is_true(m[BInRobot]):
              if abs(GDDeg)<8:
                  yield {'must': And(Su==100, BInRobot==False),
                         'wait-for': true}
              else:
                  yield {'must': And(Su==-100, BInRobot==True),
                         'wait-for': true}     
            else:
              yield {'wait-for': true}



	   
def telemUpdater():
       while True:
              getTelem()
              yield {'wait-for': true}

       
def logger():
       while True:
              yield {'wait-for': true}
              LL=m[pL]
              RR=m[pR]
              Suction=m[Su]
              if  LL.as_long() == RR.as_long():
                  stringout="player3,moveForward("+str(LL)+")\n"
                  roverSocket.sendall(stringout.encode('utf-8'))
                  print (stringout)
              if (is_false(m[BInRobot])):
                     if LL.as_long() > RR.as_long():
                         stringout="player3,moveForward(0)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                         stringout="player3,spin(100)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                         time.sleep(0.01)
                         stringout="player3,spin(0)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                     if LL.as_long() < RR.as_long():
                         stringout="player2,moveForward(0)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                         stringout="player3,spin(-100)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                         time.sleep(0.01)
                         stringout="player3,spin(0)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
              else:
                     if LL.as_long() > RR.as_long():
                         stringout="player3,moveForward(0)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                         stringout="player3,spin(100)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                         time.sleep(0.02)
                         stringout="player3,spin(0)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                     if LL.as_long() < RR.as_long():
                         stringout="player3,moveForward(0)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                         stringout="player3,spin(-100)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)
                         time.sleep(0.02)
                         stringout="player3,spin(0)\n"
                         roverSocket.sendall(stringout.encode('utf-8'))
                         print (stringout)

              stringout="player3,setSuction("+str(Suction)+")\n"
              roverSocket.sendall(stringout.encode('utf-8'))
              print (stringout)
                  

             
              
                  
		


# An execution mechanism
def run(scenarios):
    global m      # A variable where the solved model is published
    tickets = []  # A variable containing the tickets issued by the scenarios

    # Run all scenario objects to their initial yield
    for sc in scenarios:
        ticket = next(sc)       # Run the scenario to its first yield and collect the ticket
        ticket['sc'] = sc       # Maintain a pointer to the scenario in the ticket
        tickets.append(ticket)  # Add the ticket to the list of tickets

    # Main loop
    while True:
        # Compute a disjunction of may constraints and a conjunction of must constraints
        (may, must) = (False, True)
        
        for ticket in tickets:
            if 'may' in ticket:
                may = Or(may, ticket['may'])    
            if 'must' in ticket:
                must = And(must, ticket['must']) 

        # Compute a satisfying assignment and break if it does not exist
        sl = Solver()
        sl.add(And(may, must))
        if sl.check() == sat:
            m = sl.model()
        else:
            break  

        # Reset the list of tickets before rebuilding it
        oldtickets = tickets
        tickets = []
    
        # Run the scenarios to their next yield and collect new tickets 
        for oldticket in oldtickets:
            # Check if the scenario waited for the computed assignment
            if 'wait-for' in oldticket and is_true(m.eval(oldticket['wait-for'])):
                
                # Run the scenario to the next yield and collect its new ticket 
                newticket = next(oldticket['sc'], 'ended') 
    
                # Add the new ticket to the list of tickets (if the scenario didn't end)
                if not newticket == 'ended':
                    newticket['sc'] = oldticket['sc'] # Copy the pointer to the scenario 
                    tickets.append(newticket)
            else:
                # Copy the old tickets to the new list
                tickets.append(oldticket)

#leaderSocket.connect(('127.0.0.1', 9999))
#leaderSocket.send("ready\n")
#lData = leaderSocket.recv(1024)
#print ("received data:", lData)
roverSocket.connect(('127.0.0.1', 9003))
print ("after connect")
getTelem()
time.sleep(0.1)
run([
       bounds(),
       turnpowers(),
       forward(),
       spinToGoal(),
       spinToBall(),
       getBall(),
       shootBall(),
       logger(),
       telemUpdater()
])


