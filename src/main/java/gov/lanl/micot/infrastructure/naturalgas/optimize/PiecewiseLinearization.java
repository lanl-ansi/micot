package gov.lanl.micot.infrastructure.naturalgas.optimize;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;

/**
 * @author Conrado Borraz This file construct the piece wise linear segments for the PWL-type Weymouth equation constraints applied in the NG_PWL-GTNEP model -- GTNEP = Natural Gas Transmission Network Expansion Planning
 * 
 *         -- The user-defined number of linear pieces is hard-coded, which is provided in: public class PWL_GTNEP_ModelFlags extends MIPInfrastructureExpansionAlgorithmFlags private static final int DEFAULT_NUMBER_OF_PIECES = 2; -- IMPORTANT! # The parameter is somehow confusingly defined in several places throughout the extensive code. # Yet its final value is overridden/dominated by whatever the value is provided in:
 * @@ public class DefaultExpansionFactory extends MIPInfrastructureExpansionAlgorithmFactory flags.put(PWL_GTNEP_ModelFlags.NUMBER_OF_LINEAR_PIECES_KEY, 60);
 */

public class PiecewiseLinearization {
  NumberFormat formatter1 = new DecimalFormat("#0.0");
  NumberFormat formatter3 = new DecimalFormat("#0.000");
  NumberFormat formatter5 = new DecimalFormat("#0.00000");

  // public static ProjectConfiguration multiConfig2 = null; // A COMPLETE AND TOTAL HACK!
  // public static NaturalGasModel MyModel = null; // A COMPLETE AND TOTAL HACK!

  // -----------------------------
  // Problem attributes
  public int NumNodes = 0;
  public int NumCompressors = 0;
  public int NumPipes = 0;
  public int NumSources = 0;
  public int NumTerminals = 0;

  // Pressure bounds
  public ArrayList<Integer> NodeId = null;
  public ArrayList<Double> Pmin = null; // 200.0;
  public ArrayList<Double> Pmax = null; // 1000.0;

  // Flow bounds
  public ArrayList<Integer> EdgeId = null;
  public ArrayList<Integer> i = null;
  public ArrayList<Integer> j = null;
  public ArrayList<Double> MinFlow = null;
  public ArrayList<Double> MaxFlow = null;
  public ArrayList<Double> edgePmax = null;
  public ArrayList<Double> edgePmin = null;

  // InfrastrutureModeling/src/gov/lanl/infrastructure/naturalgas/optimize/PiecewiseLinearization.java
  // Old note: As expected, the discretization size must be set up accordingly to the maximum
  // value of the flow range in order to get accurate approximations.
  // Empirical results show:
  // @@ (GridPoints:Accuracy%) ==> 3:24%, 4:10%, 5:0.2%; >50:10^-4;

  // -----------------------------
  // Algorithm attributes -- JUST DEFINITION
  public int pGridPoints = 100; // Pressure discretization (just in case we wanted to linearize the compressor equation)
  public ArrayList<Double> deltaP = null; // (Pmax - Pmin) / pGridPoints;

  // Flow discretization -- JUST DEFINITION - Read above for references to its actual value
  public int xGridPoints = 0; // Accuracy: 3:24%, 4:10%, 5:0.2%; >50:10^-4;
  public ArrayList<Double> deltaX = null; // (MaxFlow - MinFlow) / xGridPoints;
  // Domain discretization for the flow grid
  public ArrayList<ArrayList<Double>> a = null; // (tt.MinFlow:Att.FlowDelta:Att.MaxFlow);

  // Piecewise-linear memory allocation
  // -----------------------------
  // Resistance factor W:
  public ArrayList<Double> W = null;

  public ArrayList<Integer> t = null;
  public ArrayList<ArrayList<Double>> u = null;
  public ArrayList<ArrayList<Double>> g = null;
  public ArrayList<ArrayList<Double>> y = null;
  public ArrayList<ArrayList<Double>> xAcc = null;
  public ArrayList<ArrayList<Double>> fxEval = null;
  public ArrayList<ArrayList<Double>> fofa = null;

  double f(double x) {
    return Math.pow(x, 2);
  }

  double xHat(double pi, double pj, double W) {
    return Math.pow((pi - pj) * (1 / W), 0.5);
  }

  public void initValues(NaturalGasModel model, PiecewiseLinearization PWL, Collection<Scenario> scenarios, int xGridPoints) throws NumberFormatException, IOException {
    boolean DisplayVerification = false;
    boolean DisplayVerification2 = false;
    boolean PrintOrNot = false;
    boolean GetMoreAccurateUB = false;
    ModelSolution s = null;
    double AvgFlow = 0.0;
    double MinFlowRange = 0.0;
    double MaxFlowRange = 0.0; // With the balancedFlow model this can be or is going to be a more sophisticated UB on x
    // -----------------------------
    // Problem attributes
    PWL.NumNodes = model.getNodes().size();
    PWL.NumCompressors = model.getCompressors().size();
    PWL.NumPipes = model.getPipes().size() + model.getResistors().size();
    PWL.NumSources = model.getWells().size();
    PWL.NumTerminals = model.getCityGates().size();

    PWL.NodeId = new ArrayList<Integer>();
    PWL.Pmin = new ArrayList<Double>();
    PWL.Pmax = new ArrayList<Double>();

    if (DisplayVerification)
      System.out.println("\n-- Verification: \n" + // p,S,D = pressure, Supply, Demand
          "\t       \t       \t p,S,D \t       \t       \tCurrent\tActual \tDesired\t   VARIABLE  \n" + "\t Node  \t Name  \t Value \t  Min  \t  Max  \t  P    \tStatus \tStatus \t    STATUS   \n" + "\t-------\t-------\t-------\t-------\t-------\t-------\t-------\t-------\t--------------");

    // Pressure bounds
    int c = 0;
    // int NumOfCityGates = 0;
    ArrayList<CityGate> EndPoints = new ArrayList<CityGate>();
    for (NaturalGasNode n : model.getNodes()) {
      PWL.NodeId.add(Integer.parseInt(n.toString()));
      PWL.Pmin.add(n.getJunction().getMinimumPressure());
      PWL.Pmax.add(n.getJunction().getMaximumPressure());

      if (DisplayVerification) {
        System.out.print("\t" + PWL.NodeId.get(c));
        if (n.getJunction().getName().length() > 7)
          System.out.print("\t" + n.getJunction().getName().substring(0, 7));
        else
          System.out.print("\t" + n.getJunction().getName());
        System.out.println("\t\t" + PWL.Pmin.get(c) + "\t" + PWL.Pmax.get(c));
      }
      // New - Feb 2015
      // -----------------------------------------------------------
      String curNode = n.toString();
      for (CityGate n1 : model.getCityGates()) {
        if (n1.toString().equals(curNode)) {
          // NumOfCityGates++;
          if (DisplayVerification2)
            System.out.println("\n*** NODE " + n1 + " (CityGate): ");
          int PipeDegree = 0;
          Collection<NaturalGasConnection> pipes = new ArrayList<NaturalGasConnection>();
          pipes.addAll(model.getPipes());
          pipes.addAll(model.getResistors());
          
          for (NaturalGasConnection edge : pipes) {
            NaturalGasNode firstNode = model.getFirstNode(edge);
            NaturalGasNode secondNode = model.getSecondNode(edge);
            if (firstNode == n || secondNode == n) {
              if (DisplayVerification2)
                System.out.println("\t " + edge + "\t(" + firstNode + "," + secondNode + ")");
              PipeDegree++;
            }
          }
          if (PipeDegree == 1) {
            EndPoints.add(n1);
          }
        }
      }
      // -----------------------------------------------------------
      c++;
    }

    // New - Feb 2015
    // -----------------------------------------------------------
    if (DisplayVerification2) {
      // System.out.println("\n*** There are [" + NumOfCityGates + ", " + model.getCityGates().size() +"] city gates: ");
      System.out.print("\n*** Out of [" + model.getCityGates().size() + "] city gates, only [" + EndPoints.size() + "] are endpoints: " + "\n    ==> { ");
      for (CityGate cg : EndPoints) {
        System.out.print(cg + " ");
      }
      System.out.println(" }");
    }
    // -----------------------------------------------------------
    // System.exit(0);

    // Flow bounds
    PWL.EdgeId = new ArrayList<Integer>();
    PWL.i = new ArrayList<Integer>();
    PWL.j = new ArrayList<Integer>();
    PWL.MinFlow = new ArrayList<Double>();
    PWL.MaxFlow = new ArrayList<Double>();
    PWL.edgePmax = new ArrayList<Double>();
    PWL.edgePmin = new ArrayList<Double>();
    PWL.W = new ArrayList<Double>();

    // -----------------------------
    // Algorithm attributes
    // Calculating flow upper bound
    // First option
    // 1double TotalGasDemand = 0.0;

    if (!GetMoreAccurateUB) {
      // First idea
      // for (CityGate n1: model.getCityGates())
      // MaxFlowRange += n1.getMinimumConsumption().doubleValue();
      // TotalGasDemand = 36;
      // Second idea -- Still a very inaccurate estimation
      // Update (Feb 2015)
      // ----------------------------------------------------------------
      for (Well n1 : model.getWells())
        if (MaxFlowRange < n1.getMaximumProduction().doubleValue())
          MaxFlowRange = n1.getMaximumProduction().doubleValue();
      MaxFlowRange *= 3;
    } else {
      // Third and most sophisticated idea
      // Major UPDATE - Feb 205
      // ===========================================================================================
      long start = System.currentTimeMillis();
      // NaturalGasModel MyModel2 = RunNGExpansion.MyModel;
      // ProjectConfiguration multiConfig2 = RunNGExpansion.MultiScenarioconfiguration2;

      // <!-- Here is where we define the algorithm constructor and parameters for creating the algorithm-->
      // <Algorithm>
      // Originally: <AlgorithmFactory>gov.lanl.iep.algorithm.naturalgas.mip.DefaultExpansionFactory</AlgorithmFactory>
      // Virtual modification: <AlgorithmFactory>gov.lanl.iep.algorithm.naturalgas.mip.BalancedFlowFactory</AlgorithmFactory>
      // </Algorithm>

      
      // RBENT, if you want to solve an optimization problem somewhere in here, best to instantiate directly.... we can talk if you think this is 
      // the best way to do it....
      
      // New setting:
//      multiConfig2.getFirstAlgorithm().setAlgorithmFactoryClass("gov.lanl.iep.algorithm.naturalgas.mip.BalancedFlowFactory"); // algorithmFactoryClass

      // String finalTestCaseName = RunNGExpansion.testCase;
      // RunNGExpansion.testCase = RunNGExpansion.testCase + "-BALANCEDFLOWS";
      // RunNGExpansion.RUN_BALANCED_FLOW_MODEL = true;

  //    try {
    //    NaturalGasMathProgramOptimizerFactory factory = (NaturalGasMathProgramOptimizerFactory) Class.forName(multiConfig2.getFirstAlgorithm().getAlgorithmFactoryClass()).newInstance();
        // get the algorithm
       // Optimizer<NaturalGasModel> algorithm = factory.constructOptimizer(multiConfig2, multiConfig2.getFirstAlgorithm(), MyModel);
      //  algorithm.addOptimizerListeners(new Vector<OptimizerListener>());
       // algorithm.solve(MyModel);
      //} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
        //e.printStackTrace();
     /// }

      // Now the question is: should we use the mean or the median of the flow set for the UB on TotalGasDemand to be discretized?
      // -- This may sound like an obscure technical question, but it really can matter.
      // -- The short answer is "it depends". To know which we should use, we must know how the data is distributed.
      // ## We follow the rule saying that the mean is the one to use with symmetrically distributed data and the median otherwise.
      // This rule may get us a more accurate reflection of an 'average' value.

      // String FlowBalancedIFwPath = RunNGExpansion.OutputFilePath + "CASE_" + RunNGExpansion.testCase + "_solution.txt";
      // s = getBalancedFlowSolution(FlowBalancedIFwPath); // Read model-solution input file - estimatedFlows

      for (int f = 0; f < s.edgeID.size(); f++)
        AvgFlow += s.x.get(f);
      AvgFlow = AvgFlow / s.edgeID.size();

      long end = System.currentTimeMillis();
      System.out.println(Thread.currentThread() + " Total execution time: " + ((end - start) / 1000.0) + " seconds");
      System.out.println("*** Balanced flow procedure: Total execution time: " + ((end - start) / 1000.0) + " seconds");
      // RunNGExpansion.PrintOutCPUtime(start, end);
      // ===========================================================================================

      // RunNGExpansion.testCase = finalTestCaseName;
      // RunNGExpansion.RUN_BALANCED_FLOW_MODEL = false;
    }

    // Flow discretization
    PWL.xGridPoints = xGridPoints; // 100;
    System.out.println("-- NUMBER_OF_LINEAR_PIECES: [" + PWL.xGridPoints + "]\n");
    if (PrintOrNot)
      System.out.println("\t ID \t pipe  \t  Pmin \t Pmax  \t   W   \t Xmin  \t Xmax  \tdelta\n" + "\t----\t-------\t-------\t-------\t-------\t-------\t-------\t------");

    PWL.deltaX = new ArrayList<Double>();
    // Domain discretization for the flow grid
    PWL.a = new ArrayList<ArrayList<Double>>();
    PWL.u = new ArrayList<ArrayList<Double>>();
    PWL.g = new ArrayList<ArrayList<Double>>();
    PWL.y = new ArrayList<ArrayList<Double>>();
    PWL.xAcc = new ArrayList<ArrayList<Double>>();
    PWL.fxEval = new ArrayList<ArrayList<Double>>();
    PWL.fofa = new ArrayList<ArrayList<Double>>();

    c = 0;
    int avgFlowUsed = 0;
    Collection<NaturalGasConnection> pipes = new ArrayList<NaturalGasConnection>();
    pipes.addAll(model.getPipes());
    pipes.addAll(model.getResistors());
    
    for (NaturalGasConnection edge : pipes) {
      NaturalGasNode node1 = model.getFirstNode(edge);
      NaturalGasNode node2 = model.getSecondNode(edge);
      String curEdge = edge.toString();
      String i = node1.toString();
      String j = node2.toString();
      // PWL.EdgeId.add(Integer.parseInt(edge.toString()));
      // PWL.i.add(Integer.parseInt(node1.toString()));
      // PWL.j.add(Integer.parseInt(node2.toString()));
      PWL.EdgeId.add(Integer.parseInt(curEdge));
      PWL.i.add(Integer.parseInt(i));
      PWL.j.add(Integer.parseInt(j));
      // Get Pmax(i) and Pmin(j)
      // Note: Technically, minP and maxP may vary at each node, so
      // we should evaluate these parameters for each end node.
      double Temp_Pmax = Math.max(node1.getJunction().getMaximumPressure(), node2.getJunction().getMaximumPressure());
      double Temp_Pmin = Math.min(node1.getJunction().getMinimumPressure(), node2.getJunction().getMinimumPressure());
      double Pmax = Math.pow(Temp_Pmax, 2);
      double Pmin = Math.pow(Temp_Pmin, 2);
      PWL.edgePmax.add(Pmax);
      PWL.edgePmin.add(Pmin);

      double Wij = edge.getResistance(); // ResistanceFactor
      // double MinFlowRange = -TotalGasDemand;
      // Before:
      // double MaxFlowRange = Math.pow((Pmax - Pmin)*(1/Wij), 0.5);
      // -- The problem with the above setting is that it can become extremely large
      // when working with small load values, thus requiring a large number of
      // discretization points to approximate the unknown flow in a more accurate way
      // -- After some studying, I've realized this is even worst. (Feb 2015)
      // Now: (Still not very accurate as of Feb 2015)
      // 1 double MaxFlowRange = TotalGasDemand; // This a more conservative upper bound on x

      // Update: Feb 2015 (Hopefully a more accurate way to estimate MaxFlow through the pipes
      // =======================================================================================
      if (GetMoreAccurateUB) {
        for (int f = 0; f < s.edgeID.size(); f++) {
          if ((s.i.get(f).equals(i) && s.j.get(f).equals(j)) || (s.i.get(f).equals(j) && s.j.get(f).equals(i))) {
            MaxFlowRange = s.x.get(f) * 1.1; // *3, *10;
            break;
          }
        }
        if (MaxFlowRange <= 1e-2) {
          MaxFlowRange = AvgFlow * 1.1;
          avgFlowUsed++;
        }
      }
      // int IsPipeActive = s.getEdgeIndex(curEdge); // check out whether or not there is an estimated flow for the current pipe
      // if (IsPipeActive != -1)
      // if (firstNode.equals(curNode) || secondNode.equals(curNode)) {
      // // Retrieve flow x through the arc
      // double x = s.getFlow(curEdge);
      // if ( (firstNode.equals(curNode) && x > 0) ||
      // (secondNode.equals(curNode) && x < 0) )
      // OutgoingGas += Math.abs(x);
      // if ( (firstNode.equals(curNode) && x < 0) ||
      // (secondNode.equals(curNode) && x > 0) )
      // IncomingGas += Math.abs(x);
      // }

      // =======================================================================================

      PWL.W.add(Wij);
      PWL.MinFlow.add(MinFlowRange);
      PWL.MaxFlow.add(MaxFlowRange);

      // Flow discretization: (Accuracy) 3:24%, 4:10%, 5:0.2%; >50:10^-4;
      double deltaT = (MaxFlowRange - MinFlowRange) / PWL.xGridPoints;
      PWL.deltaX.add(deltaT);

      if (PrintOrNot)
        System.out.println("\t" + PWL.EdgeId.get(c) + "\t(" + PWL.i.get(c) + "," + PWL.j.get(c) + ")\t" + formatter1.format(PWL.edgePmin.get(c)) + "\t" + formatter1.format(PWL.edgePmax.get(c)) + "\t" + formatter5.format(PWL.W.get(c)) + "\t" + formatter1.format(PWL.MinFlow.get(c)) + "\t" + formatter1.format(PWL.MaxFlow.get(c)) + "\t" + formatter3.format(PWL.deltaX.get(c)));
      // Domain discretization for the flow grid on edge a (row)
      PWL.a.add(new ArrayList<Double>());
      PWL.fofa.add(new ArrayList<Double>());

      for (int p = 0; p <= PWL.xGridPoints; p++) {
        double point = MinFlowRange + deltaT * p;
        if (point > MaxFlowRange)
          point = MaxFlowRange;
        PWL.a.get(c).add(point);
        double fofaEval = f(point);
        PWL.fofa.get(c).add(fofaEval);
      }
      if (PrintOrNot) {
        // System.out.println("\n\tOUT OF CURIOSITY, #discretization points: [" + PWL.xGridPoints + "], deltaT value: [" + deltaT +"]");
        DisplayGridPoints(PWL.a.get(c), "\ta");
        DisplayGridPoints(PWL.fofa.get(c), "\tf(a)");
      }

      // ------------------------
      PWL.u.add(new ArrayList<Double>());
      PWL.g.add(new ArrayList<Double>());
      PWL.y.add(new ArrayList<Double>());
      PWL.xAcc.add(new ArrayList<Double>());
      PWL.fxEval.add(new ArrayList<Double>());

      PWL.u.get(c).add(0.0);
      PWL.g.get(c).add(0.0);
      for (int p = 1; p <= PWL.xGridPoints; p++) {
        double LinSegLength = PWL.a.get(c).get(p) - PWL.a.get(c).get(p - 1);
        PWL.u.get(c).add(LinSegLength);
        double gofLength = PWL.fofa.get(c).get(p) - PWL.fofa.get(c).get(p - 1);
        PWL.g.get(c).add(gofLength);
      }
      if (PrintOrNot) {
        DisplayGridPoints(PWL.u.get(c), "\tu");
        DisplayGridPoints(PWL.g.get(c), "\tg");
      }
      c++;
    }
    System.out.println("\n**** avgFlowUsed = [" + avgFlowUsed + "]\n");
    // System.exit(0);
  }

  private void DisplayGridPoints(ArrayList<Double> xGrid, String ArrayName) {
    int xGridPoints = xGrid.size();
    System.out.print("\t\t" + ArrayName + ": {");
    for (int p = 0; p < xGridPoints; p++)
      System.out.print("\t" + formatter3.format(xGrid.get(p)));
    System.out.println("\t}");
  }

  // Update: Feb 2015
  // ****************************
  // ** Flow SOLUTION CLASS **
  // ****************************
  class ModelSolution {
    // Edges' info
    ArrayList<String> edgeID; // Pipes
    ArrayList<String> i;
    ArrayList<String> j;
    ArrayList<Double> x; // flow

    // constructor
    ModelSolution() {
      edgeID = new ArrayList<String>();
      i = new ArrayList<String>();
      j = new ArrayList<String>();
      x = new ArrayList<Double>();
    }

    public double getFlow(String pipe) {
      int index = edgeID.indexOf(pipe);
      if (index == -1) {
        return index;
        // System.out.println("\n\n\t-- WARNING! Edge ID [" + pipe + "] no found in << edgeID >> array list.");
        // System.exit(1);
      }
      return x.get(index);
    }

    public int getEdgeIndex(String pipe) {
      return edgeID.indexOf(pipe); // index;
    }
  }

  public ModelSolution getBalancedFlowSolution(String IFwPath) throws NumberFormatException, IOException {
    boolean PrintOrNot = false;
    ModelSolution s = new ModelSolution();
    System.out.println("-- Retrieving balanced-flow model solution:" + "   @@ " + IFwPath);
    BufferedReader br = new BufferedReader(new FileReader(IFwPath));
    String line = null;
    if (PrintOrNot)
      System.out.println("\n\tId \t pipe  \t   Value  \n\t---\t-------\t--------------");
    int c = 0;
    while ((line = br.readLine()) != null) {
      if (line.isEmpty())
        break;
      String[] vals = line.trim().split("\t");
      s.edgeID.add(vals[0]);
      s.i.add(vals[1]);
      s.j.add(vals[2]);
      double val = Math.abs(Double.parseDouble(vals[3]));
      s.x.add(val);
      if (PrintOrNot)
        System.out.println("\t" + s.edgeID.get(c) + "\t(" + s.i.get(c) + "," + s.j.get(c) + ")\t " + s.x.get(c));
      c++;
    }
    System.out.println("\n\t-- TOTAL NUM OF FLOW VALUES RECOVERED = [" + c + "]");
    br.close();
    return s;
  }

}
