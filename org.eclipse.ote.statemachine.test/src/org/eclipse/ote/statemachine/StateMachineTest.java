/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.statemachine;


import org.junit.Assert;
import org.junit.Test;

public class StateMachineTest {

   @Test
   public void test() throws Exception {
      StateMachine sm = new StateMachine("Stoplight");
      
      Green green = new Green();
      Red red = new Red();
      Yellow yellow = new Yellow();
      Change change = new Change(sm, Object.class);
      
      sm.newTransition(green, change, yellow);
      sm.newTransition(yellow, change, red);
      sm.newTransition(red, change, green);
      sm.setDefaultInitialState(red);
      sm.initialize();
      
      Assert.assertEquals(red, sm.getCurrentState());
      sm.addToQueue(change);
      sm.processInput();
      Assert.assertEquals(green, sm.getCurrentState());
      sm.addToQueue(change);
      sm.processInput();
      Assert.assertEquals(yellow, sm.getCurrentState());
      sm.addToQueue(change);
      sm.processInput();
      Assert.assertEquals(red, sm.getCurrentState());
      
      sm.stop();
   }
   
   @Test
   public void testTransitionTableConflict() throws Exception {
      StateMachine sm = new StateMachine("Stoplight");
      
      Green green = new Green();
      Red red = new Red();
      Yellow yellow = new Yellow();
      Change change = new Change(sm, Object.class);
      
      sm.newTransition(red, change, green);
      sm.newTransition(green, change, yellow);
      sm.newTransition(yellow, change, red);
      try{
         sm.newTransition(red, change, yellow);
         Assert.fail();
      } catch (Exception ex){
      }
      sm.stop();
   }
   
   @Test
   public void testWaits() throws Exception {
      final StateMachine sm = new StateMachine("Stoplight");
      
      Green green = new Green();
      Red red = new Red();
      Yellow yellow = new Yellow();
      Change change = new Change(sm, Object.class);
      
      sm.setDefaultInitialState(red);
      sm.newTransition(green, change, yellow);
      sm.newTransition(yellow, change, red);
      sm.newTransition(red, change, green);
      sm.initialize();
      
      sm.start();
      
      Assert.assertTrue(!sm.waitForCompletion(100));
      
      sm.stop();
      
      Assert.assertTrue(sm.waitForCompletion(100));
      
      
      sm.start();
      
      Assert.assertTrue(!sm.waitForCompletion(100));
      
      sm.murder();
      
      sm.waitForCompletion();
      
      sm.resetToInitialState();
      sm.start();
      
      red.submitDelayedInput(change, 50);
      Assert.assertEquals(red, sm.getCurrentState());
      Thread.sleep(100);
      Assert.assertEquals(green, sm.getCurrentState());
      red.submitDelayedInput(change, 50);
      Thread.sleep(10);
      red.cancelDelayedInputs();
      Thread.sleep(100);
      Assert.assertEquals(green, sm.getCurrentState());
      sm.stop();
      Thread.sleep(10);
      Assert.assertTrue(sm.waitForCompletion(100));
      
      sm.start();
      new Thread(new Runnable() {
         
         @Override
         public void run() {
            // TODO Auto-generated method stub
            try {
               Thread.sleep(10);
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } finally {
               try {
                  sm.stop();
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      }).start();
      sm.waitForCompletion();
      
   }
   
   @Test
   public void hierarchyTest() throws Exception{
      StateMachine parent = new StateMachine("Stoplight");
      
      Green green = new Green();
      Red red = new Red();
      Yellow yellow = new Yellow();
      Change change = new Change(parent, Object.class);
      Change internalExit = new Change(parent, Integer.class);
      Change internalExit2 = new Change(parent, Integer.class);
      
      ChildStateMachineState child = new Hierarchy(parent);
      
      //begin hier 2
      ChildStateMachineState grandchild = new Hierarchy(parent);
      Blue blud2 = new Blue();
      Purple purple2 = new Purple(internalExit2);
      
      grandchild.setDefaultInitialState(blud2);
      grandchild.newTransition(blud2, change, purple2);
      grandchild.newTransition(purple2, change, blud2);
      //end hier 2
      
      // Begin hier 1
      Blue blud = new Blue();
      Purple purple = new Purple(internalExit);

      child.setDefaultInitialState(blud);
      child.newTransition(grandchild, internalExit2, purple);
      child.newTransition(blud, change, grandchild);
      child.newTransition(purple, change, blud);
      // end hier 1
      
      // begin master machine
      parent.setDefaultInitialState(green);
      parent.newTransition(green, change, yellow);
      parent.newTransition(yellow, change, red);
      parent.newTransition(red, change, child);
      parent.newTransition(child, internalExit, green);
      parent.initialize();
      // end master machine
      
      parent.addToQueue(change);
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(yellow, parent.getCurrentState());
      parent.addToQueue(change);
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(red, parent.getCurrentState());
      parent.addToQueue(change);
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(child, parent.getCurrentState());
      Assert.assertEquals(blud, ((Hierarchy)child).getCurrentState());
      parent.addToQueue(change);
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(child, parent.getCurrentState());
      Assert.assertEquals(grandchild, ((Hierarchy)child).getCurrentState());
      Assert.assertEquals(blud2, ((Hierarchy)grandchild).getCurrentState());
      parent.addToQueue(change);
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(purple2, ((Hierarchy)grandchild).getCurrentState());
      Assert.assertEquals(grandchild, ((Hierarchy)child).getCurrentState());
      Assert.assertEquals(child, parent.getCurrentState());
      parent.addToQueue(change);
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(blud2, ((Hierarchy)grandchild).getCurrentState());
      Assert.assertEquals(grandchild, ((Hierarchy)child).getCurrentState());
      Assert.assertEquals(child, parent.getCurrentState());
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(blud2, ((Hierarchy)grandchild).getCurrentState());
      Assert.assertEquals(purple, ((Hierarchy)child).getCurrentState());
      Assert.assertEquals(child, parent.getCurrentState());
      parent.addToQueue(change);
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(blud2, ((Hierarchy)grandchild).getCurrentState());
      Assert.assertEquals(blud, ((Hierarchy)child).getCurrentState());
      Assert.assertEquals(child, parent.getCurrentState());
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(green, parent.getCurrentState());
      parent.addToQueue(change);
      Assert.assertEquals(1, parent.queueSize());
      parent.processInput();
      Assert.assertEquals(yellow, parent.getCurrentState());
      parent.stop();
   }
   
   @Test
   public void hierarchyTestInThread() throws Exception{
      StateMachine parent = new StateMachine("Stoplight", true);
      
      Green green = new Green("green");
      Red red = new Red("red");
      Yellow yellow = new Yellow("yellow");
      Change change = new Change(parent, Object.class, "change");
      Change internalExit = new Change(parent, Integer.class, "internalExit1");
      Change internalExit2 = new Change(parent, Integer.class, "internalExit2");
      
      ChildStateMachineState child = new Hierarchy(parent, "child");
      
      //begin hier 2
      ChildStateMachineState grandchild = new Hierarchy(parent, "grandchild");
      Blue blud2 = new Blue("blud2");
      Purple purple2 = new Purple(internalExit2, "purple2");
      
      grandchild.setDefaultInitialState(blud2);
      grandchild.newTransition(blud2, change, purple2);
      grandchild.newTransition(purple2, change, blud2);
      //end hier 2
      
      // Begin hier 1
      Blue blud = new Blue("blud");
      Purple purple = new Purple(internalExit, "purple");

      child.setDefaultInitialState(blud);
      child.newTransition(grandchild, internalExit2, purple);
      child.newTransition(blud, change, grandchild);
      child.newTransition(purple, change, blud);
      // end hier 1
      
      // begin master machine
      parent.setDefaultInitialState(green);
      parent.newTransition(green, change, yellow);
      parent.newTransition(yellow, change, red);
      parent.newTransition(red, change, child);
      parent.newTransition(child, internalExit, green);
      parent.initialize();
      // end master machine
      
      parent.addToQueue(change);
      parent.processUntilEmpty();
      Assert.assertEquals(yellow, parent.getCurrentState());
      
      parent.addToQueue(change);
      parent.processUntilEmpty();
      Assert.assertEquals(red, parent.getCurrentState());
      
      parent.addToQueue(change);
      parent.processUntilEmpty();
      Assert.assertEquals(child, parent.getCurrentState());
      Assert.assertEquals(blud, ((Hierarchy)child).getCurrentState());
      
      parent.addToQueue(change);
      parent.processUntilEmpty();
      Assert.assertEquals(child, parent.getCurrentState());
      Assert.assertEquals(grandchild, ((Hierarchy)child).getCurrentState());
      Assert.assertEquals(blud2, ((Hierarchy)grandchild).getCurrentState());
      
      parent.addToQueue(change);
      parent.processUntilEmpty();
      Assert.assertEquals(child, parent.getCurrentState());
      Assert.assertEquals(grandchild, ((Hierarchy)child).getCurrentState());
      Assert.assertEquals(purple2, ((Hierarchy)grandchild).getCurrentState());
      
      parent.addToQueue(change);
      parent.processUntilEmpty();
      Assert.assertEquals(child, parent.getCurrentState());
      Assert.assertEquals(purple, ((Hierarchy)child).getCurrentState());
      Assert.assertEquals(blud2, ((Hierarchy)grandchild).getCurrentState());
      
      parent.addToQueue(change);
      parent.processUntilEmpty();
      Assert.assertEquals(green, parent.getCurrentState());
      Assert.assertEquals(blud2, ((Hierarchy)grandchild).getCurrentState());
      Assert.assertEquals(blud, ((Hierarchy)child).getCurrentState());
      
      parent.addToQueue(change);
      parent.processUntilEmpty();
      Assert.assertEquals(yellow, parent.getCurrentState());
      parent.stop();
   }
   
   private static class ChildStateMachine {
      
      private Blue blue;
      private Change internalExit;
      private Purple purple;
      private Hierarchy child;
      private GrandChildStateMachine grandChild;

      public ChildStateMachine(StateMachine parent, GrandChildStateMachine grandChild, Change change) throws Exception{
         this.grandChild = grandChild;
         internalExit = new Change(parent, Integer.class);
         child = new Hierarchy(parent);
         blue = new Blue();
         purple = new Purple(internalExit);

         child.setDefaultInitialState(blue);
         child.newTransition(blue, change, grandChild.get());
         child.newTransition(grandChild.get(), grandChild.getExitInput(), purple);
         child.newTransition(purple, change, blue);
      }
      
      public ChildStateMachineState get(){
         return child;
      }
      
      public BaseInput getExitInput(){
         return internalExit;
      }

      public BaseState getBlue() {
         return blue;
      }

      public BaseState getCurrentState() {
         return child.getCurrentState();
      }

      public BaseState getGrandchild() {
         return grandChild.get();
      }

      public BaseState getPurple() {
         return purple;
      }
   }
   
   private static class GrandChildStateMachine {
      
      private Hierarchy grandchild;
      private Blue blue;
      private Purple purple;
      
      private Change internalExit;

      public GrandChildStateMachine(StateMachine parent, Change change) throws Exception{
         grandchild = new Hierarchy(parent);
         internalExit = new Change(parent, Integer.class);
         blue = new Blue();
         purple = new Purple(internalExit);
         
         grandchild.setDefaultInitialState(blue);
         grandchild.newTransition(blue, change, purple);
         grandchild.newTransition(purple, change, blue);
      }
      
      public ChildStateMachineState get(){
         return grandchild;
      }
      
      public BaseInput getExitInput(){
         return internalExit;
      }

      public BaseState getBlue() {
         return blue;
      }

      public BaseState getCurrentState() {
         return grandchild.getCurrentState();
      }

      public BaseState getPurple() {
         return purple;
      }
   }
   
   private static class ParentStateMachine {
      
      private Green green;
      private Red red;
      private Yellow yellow;
      private StateMachine parent;
      private ChildStateMachine child;

      public ParentStateMachine(StateMachine parent, ChildStateMachine child, Change change) throws Exception{
         this.parent = parent;
         this.child = child;
         green = new Green();
         red = new Red();
         yellow = new Yellow();
         
         parent.setDefaultInitialState(green);
         parent.newTransition(green, change, yellow);
         parent.newTransition(yellow, change, red);
         parent.newTransition(red, change, child.get());
         parent.newTransition(child.get(), child.getExitInput(), green);
      }

      public BaseState getYellow() {
         return yellow;
      }

      public BaseState getRed() {
         return red;
      }

      public BaseState getCurrentState() {
         return parent.getCurrentState();
      }

      public BaseState getGreen() {
         return green;
      }

      public Object getChild() {
         return child.get();
      }
     
   }
   
   @Test
   public void hierarchyTestAsClasses() throws Exception{
      
      StateMachine masterStateMachine = new StateMachine("Stoplight");
      Change change = new Change(masterStateMachine, Object.class);
      
      GrandChildStateMachine grandChild = new GrandChildStateMachine(masterStateMachine, change);
      ChildStateMachine child = new ChildStateMachine(masterStateMachine, grandChild, change);
      ParentStateMachine parent = new ParentStateMachine(masterStateMachine, child, change);

      masterStateMachine.initialize();
      
      masterStateMachine.addToQueue(change);
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(parent.getYellow(), parent.getCurrentState());
      masterStateMachine.addToQueue(change);
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(parent.getRed(), parent.getCurrentState());
      masterStateMachine.addToQueue(change);
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(child.get(), parent.getCurrentState());
      Assert.assertEquals(child.getBlue(), child.getCurrentState());
      masterStateMachine.addToQueue(change);
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(child.get(), parent.getCurrentState());
      Assert.assertEquals(child.getGrandchild(), child.getCurrentState());
      Assert.assertEquals(grandChild.getBlue(), grandChild.getCurrentState());
      masterStateMachine.addToQueue(change);
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(grandChild.getPurple(), grandChild.getCurrentState());
      Assert.assertEquals(child.getGrandchild(), child.getCurrentState());
      Assert.assertEquals(child.get(), parent.getCurrentState());
      masterStateMachine.addToQueue(change);
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(grandChild.getBlue(),grandChild.getCurrentState());
      Assert.assertEquals(child.getGrandchild(), child.getCurrentState());
      Assert.assertEquals(child.get(), parent.getCurrentState());
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(grandChild.getBlue(), grandChild.getCurrentState());
      Assert.assertEquals(child.getPurple(), child.getCurrentState());
      Assert.assertEquals(child.get(), parent.getCurrentState());
      masterStateMachine.addToQueue(change);
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(grandChild.getBlue(), grandChild.getCurrentState());
      Assert.assertEquals(child.getBlue(), child.getCurrentState());
      Assert.assertEquals(parent.getChild(), parent.getCurrentState());
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(parent.getGreen(), parent.getCurrentState());
      masterStateMachine.addToQueue(change);
      Assert.assertEquals(1, masterStateMachine.queueSize());
      masterStateMachine.processInput();
      Assert.assertEquals(parent.getYellow(), parent.getCurrentState());
      masterStateMachine.stop();
   }

}
