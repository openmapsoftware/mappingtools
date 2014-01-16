package com.openMap1.mapper.core;

public class MapperException extends Exception {

protected String MDLExType;

// to remove some compiler warning
static final long serialVersionUID = 0;

  public MapperException(String message) {
  super(message);
  }

}
