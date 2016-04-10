package de.gedoplan.beantrial.ejbremoteexception.service;

import de.gedoplan.beantrial.ejbremoteexception.entity.Dummy;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
public class ThrowExceptionServiceBean implements ThrowExceptionService
{
  @PersistenceContext
  EntityManager entityManager;

  @Override
  public void throwNoException()
  {
  }

  @Override
  public void throwMyException() throws MyException
  {
    throw new MyException();
  }

  @Override
  public void throwNullPointerException()
  {
    throw new NullPointerException();
  }

  @Override
  public void throwSqlException()
  {
    this.entityManager.persist(new Dummy(4711, "SiebenundvierzigElf"));
    this.entityManager.persist(new Dummy(4712, "SiebenundvierzigElf"));
  }
}
