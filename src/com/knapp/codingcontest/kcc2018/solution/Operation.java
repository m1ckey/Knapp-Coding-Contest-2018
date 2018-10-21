package com.knapp.codingcontest.kcc2018.solution;

// KCC2018-java
// Created by Michael Krickl in 2018


import com.knapp.codingcontest.kcc2018.data.Container;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Location;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Position;

public class Operation
{

  public Operation(OperationType type, Location location, Container container)
  {
    this.type = type;
    this.location = location;
    this.container = container;
  }

  public OperationType type;
  public Location location;
  public Container container;
}
