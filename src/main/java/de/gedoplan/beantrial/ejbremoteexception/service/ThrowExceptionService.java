package de.gedoplan.beantrial.ejbremoteexception.service;

import javax.ejb.Remote;

@Remote
public interface ThrowExceptionService
{
  public void throwNoException();

  public void throwMyException() throws MyException;

  public void throwNullPointerException();

  public void throwSqlException();
}
