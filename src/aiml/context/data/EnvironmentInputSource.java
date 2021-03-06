/*
    jaiml - java AIML library
    Copyright (C) 2009  Kim Sullivan
    
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
    
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package aiml.context.data;

import java.util.EmptyStackException;

import aiml.environment.Environment;

/**
 * This input data source uses the stack provided in the environment to store
 * the data.
 * 
 * @author Kim Sullivan
 * 
 */
public class EnvironmentInputSource implements InputDataSource<String> {

  @Override
  public String getValue(Environment e) {
    return e.getInput();
  }

  @Override
  public void pop(Environment e) throws EmptyStackException {
    e.popInput();
  }

  @Override
  public void push(String input, Environment e) {
    e.pushInput(input);
  }

}
