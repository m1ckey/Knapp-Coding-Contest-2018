package com.knapp.codingcontest.kcc2018.solution;

// KCC2018-java
// Created by Michael Krickl in 2018


import java.util.ArrayList;
import java.util.List;

public class Path implements Comparable<Path>
{
  public Path(List<Operation> operations, double cost) {
    this.operations = operations;
    this.cost = cost;
  }

  public List<Operation> operations;
  public double cost;

  @Override
  public int compareTo(Path that)
  {
    if (this.cost - that.cost < 0) return -1;
    if (this.cost - that.cost > 0) return 1;
    return 0;
  }
}
