package de.gedoplan.beantrial.ejbremoteexception.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name") )
public class Dummy
{
  @Id
  private int    id;
  private String name;

  protected Dummy()
  {
  }

  public Dummy(int id, String name)
  {
    this.id = id;
    this.name = name;
  }

  @Override
  public String toString()
  {
    return "Dummy [id=" + this.id + ", name=" + this.name + "]";
  }
}
