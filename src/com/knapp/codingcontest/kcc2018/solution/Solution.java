/* -*- java -*- ************************************************************************** *
 *
 *                     Copyright (C) KNAPP AG
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.kcc2018.solution;

import java.util.*;

import com.knapp.codingcontest.kcc2018.data.Container;
import com.knapp.codingcontest.kcc2018.data.Institute;
import com.knapp.codingcontest.kcc2018.data.Order;
import com.knapp.codingcontest.kcc2018.warehouse.Shuttle;
import com.knapp.codingcontest.kcc2018.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2018.warehouse.WorkStation;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Aisle;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Location;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Position;

/**
 * This is the code YOU have to provide
 *
 * @param warehouse all the operations you should need
 */
public class Solution
{

  /**
   * TODO: Your name
   */
  public static final String PARTICIPANT_NAME = "Michael Krickl";

  /**
   * TODO: The Id of your institute - please refer to the handout
   */
  public static final Institute PARTICIPANT_INSTITUTION = Institute.HTL_Kaindorf;

  // ----------------------------------------------------------------------------

  private final Warehouse warehouse;

  // ----------------------------------------------------------------------------

  public Solution(final Warehouse warehouse)
  {

    this.warehouse = warehouse;
  }

  // ----------------------------------------------------------------------------

  private final List<Location> allLocations = new ArrayList<>();
  private Shuttle shuttle;
  private WorkStation workStation;

  public void runWarehouseOperations()
  {

    System.out.println("### Your output starts here");

    //  ==> CODE YOUR SOLUTION HERE !!!");

    // collaborators
    shuttle = warehouse.getShuttle();
    workStation = warehouse.getWorkStation();

    // information
    final Warehouse.Characteristics c = warehouse.getCharacteristics();
    final int numberOfAisles = c.getNumberOfAisles();
    final int numberOfPositionsPerAisle = c.getNumberOfPositionsPerAisle();
    final int locationDepth = c.getLocationDepth();

    final Collection<Container> allContainers = warehouse.getAllContainers();
    final List<Order> orders = warehouse.getOrders();
    for(Aisle a : warehouse.getAisles())
    {
      allLocations.addAll(a.getLocations());
    }

    int currentOrder = 0;

    Location excludeLocation = null;

    while(true)
    {

      Order order = orders.stream().filter(o -> o.hasRemainingItems()).findFirst().orElse(null);

      if(order == null)
      {
        break;
      }


      System.out.println("handling order" + order.toString());

      String product = order.getProductCode();

      if(shuttle.getLoadedContainer() != null)
      {
        storeToNearest();
      }

      Path path = warehouse.getAllContainers()
               .stream()
               .filter(con -> con.getProductCode() != null)
               .filter(con -> con.getProductCode().equals(product))
               .map(con -> makePathFor(order, con))
              .sorted()
              .findFirst()
              .orElse(null);

      for(Operation op : path.operations){
        switch(op.type) {
          case MOVE:
            if(op.location == null) {
              shuttle.moveToPosition(workStation);
            } else {
              shuttle.moveToPosition(op.location.getPosition());
            }
            break;
          case STORE:

            shuttle.storeTo(op.location);

            break;
          case PICK:

            workStation.pickOrder(order);
            break;
          case LOAD:

            shuttle.loadFrom(op.location, op.container);

            break;
        }
      }
 /*
      Container container = warehouse.getAllContainers()
                                     .stream()
                                     .filter(con -> con.getProductCode() != null)
                                     .filter(con -> con.getProductCode().equals(product))
                                     .filter(con -> con.getLocation().isReachable(con))
                                     .sorted(new Comparator<Container>()
                                     {

                                       @Override
                                       public int compare(Container c1, Container c2)
                                       {

                                         return (int) (
                                             warehouse.calcMoveCost(shuttle.getCurrentPosition(),
                                                 c1.getLocation().getPosition()) -
                                             warehouse.calcMoveCost(shuttle.getCurrentPosition(),
                                                 c2.getLocation().getPosition()));

                                       }
                                     })
                                     .findFirst()
                                     .orElse(null);


      // no reachable container
      if(container == null)
      {
        container = warehouse.getAllContainers()
                             .stream()
                             .filter(con -> con.getProductCode() != null)
                             .filter(con -> con.getProductCode().equals(product))
                             .sorted(new Comparator<Container>()
                             {

                               @Override
                               public int compare(Container c1, Container c2)
                               {

                                 return (int) (warehouse.calcMoveCost(shuttle.getCurrentPosition(),
                                     c1.getLocation().getPosition()) -
                                               warehouse.calcMoveCost(shuttle.getCurrentPosition(),
                                                   c2.getLocation().getPosition()));

                               }
                             })
                             .findFirst()
                             .orElse(null);
        shuttle.moveToPosition(container.getLocation().getPosition());
        shuttle.loadFrom(container.getLocation(),
            container.getLocation().getContainers().stream().findFirst().orElse(null));

        excludeLocation = container.getLocation();
        continue;
      }

      shuttle.moveToPosition(container.getLocation().getPosition());
      shuttle.loadFrom(container.getLocation(), container);
      shuttle.moveToPosition(workStation);

      workStation.pickOrder(order); */
    }

    Location endlagern = allLocations.stream()
                                     .filter(l -> l.getRemainingContainerCapacity() != 0)
                                     .sorted(new Comparator<Location>()
                                     {

                                       @Override
                                       public int compare(Location l1, Location l2)
                                       {

                                         return (int) (
                                             warehouse.calcMoveCost(shuttle.getCurrentPosition(),
                                                 l1.getPosition()) -
                                             warehouse.calcMoveCost(shuttle.getCurrentPosition(),
                                                 l2.getPosition()));
                                       }
                                     })
                                     .findFirst()
                                     .orElse(null);
    shuttle.moveToPosition(endlagern.getPosition());
    shuttle.storeTo(endlagern);

    System.out.println("### Your output stops here");

    System.out.println("");
    System.out.println(String.format("--> Total operation cost        : %10d",
        warehouse.getCurrentOperationsCost()));
    System.out.println(String.format("--> Total unfinished order cost : %10d",
        warehouse.getCurrentUnfinishedOrdersCost()));
    System.out.println(
        String.format("--> Total cleanup cost          : %10d", warehouse.getCurrentCleanupCost()));
    System.out.println(String.format("                                  ------------"));
    System.out.println(
        String.format("==> TOTAL COST                  : %10d", warehouse.getCurrentTotalCost()));
    System.out.println(String.format("                                  ============"));
  }

  private Path makePathFor(Order order, Container container) {

    long cost = 0;
    List<Operation> operations = new ArrayList<>();
    Position shuttlePosition = shuttle.getCurrentPosition();

    Location location = container.getLocation();

    cost += warehouse.calcMoveCost(shuttlePosition, location.getPosition());
    operations.add(new Operation(OperationType.MOVE, location, null));
    shuttlePosition = location.getPosition();

    if(!location.isReachable(container)) {

      final Position finalShuttlePosition = shuttlePosition;

      Location nearestEmpty = allLocations
          .stream()
          .filter(l -> l.getRemainingContainerCapacity() != 0)
          .sorted(new Comparator<Location>()
          {
            @Override
            public int compare(Location l1, Location l2)
            {

              return (int) (warehouse.calcMoveCost(
                  finalShuttlePosition,
                  l1.getPosition()) - warehouse.calcMoveCost(
                  finalShuttlePosition,
                  l2.getPosition()));
            }
          })
          .findFirst()
          .orElse(null);

      Container loadedContainer = location.getContainers().stream().findFirst().orElse(null);
      operations.add(new Operation(OperationType.LOAD, location, loadedContainer));

      cost += warehouse.calcMoveCost(shuttlePosition, nearestEmpty.getPosition());
      operations.add(new Operation(OperationType.MOVE, nearestEmpty, null));
      shuttlePosition = nearestEmpty.getPosition();

      operations.add(new Operation(OperationType.STORE, nearestEmpty, loadedContainer));

      cost += warehouse.calcMoveCost(shuttlePosition, location.getPosition());
      operations.add(new Operation(OperationType.MOVE, location, null));
      shuttlePosition = location.getPosition();
    }

    operations.add(new Operation (OperationType.LOAD, location, container));

    cost += warehouse.calcMoveCost(shuttlePosition, workStation);
    operations.add(new Operation(OperationType.MOVE, null, null));

    operations.add(new Operation(OperationType.PICK, null, null));

    int quantitiyLeft =  order.getRemainingQuantity() - container.getQuantity();

    Path path = new Path(operations, cost);

    if(container.getQuantity() == 0) {
      path.cost = Double.MAX_VALUE;
      return path;
    }

    if(quantitiyLeft > 0) {
      path.cost = path.cost / container.getQuantity();
    }
    else {
      path.cost = path.cost / order.getRemainingQuantity();
    }

    return path;
  }

  private void storeToNearest() {
    storeToNearest(new ArrayList<>());
  }

  private void storeToNearest(List<Location> excludedLocations) {

    Location storeLocation = allLocations.stream()
                                         .filter(l -> l.getRemainingContainerCapacity() != 0)
                                         .filter(l -> !excludedLocations.contains(l))
                                         .sorted(new Comparator<Location>()
                                         {

                                           @Override
                                           public int compare(Location l1, Location l2)
                                           {

                                             return (int) (warehouse.calcMoveCost(
                                                 shuttle.getCurrentPosition(),
                                                 l1.getPosition()) - warehouse.calcMoveCost(
                                                 shuttle.getCurrentPosition(),
                                                 l2.getPosition()));
                                           }
                                         })
                                         .findFirst()
                                         .orElse(null);

    shuttle.moveToPosition(storeLocation.getPosition());
    shuttle.storeTo(storeLocation);
  }

  // ----------------------------------------------------------------------------

  private void apis()
  {
    // collaborators
    final Shuttle shuttle = warehouse.getShuttle();
    final WorkStation workStation = warehouse.getWorkStation();

    // information
    final Warehouse.Characteristics c = warehouse.getCharacteristics();
    final int numberOfAisles = c.getNumberOfAisles();
    final int numberOfPositionsPerAisle = c.getNumberOfPositionsPerAisle();
    final int locationDepth = c.getLocationDepth();

    // information that change with operations! (orders/containers/locations/...)
    final Collection<Location> locations = warehouse.getAisle(0).getLocations();
    final Location location = warehouse.getAisle(0).getLocation(0, Aisle.Side.Left);


    final Collection<Container> allContainers = warehouse.getAllContainers();

    final long currentTotalCost = warehouse.getCurrentTotalCost();

    final Container loadedContainer = shuttle.getLoadedContainer();
    final Position currentPosition = shuttle.getCurrentPosition();
    final boolean isAtWorkStation = shuttle.isAtWorkStation();

    //
    // operations
    shuttle.moveToPosition(workStation);
    shuttle.moveToPosition(location.getPosition());
    final Container expected = location.getContainers().get(0);
    shuttle.loadFrom(location, expected);

    final Order order = null;
    workStation.pickOrder(order);

    shuttle.storeTo(location);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
