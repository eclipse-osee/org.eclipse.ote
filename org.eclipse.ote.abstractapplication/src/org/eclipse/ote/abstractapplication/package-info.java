/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

//@formatter:off
/**
 * This package provides a basic application framework and command line processing facility. To make use of this package
 * the programmers needs to implement the following as shown in the figure &quot;AbstractApplication Sample Usage&quot;:
 * <ul>
 * <li>An extension of the {@link AbstractApplication} class with a static main method.</li>
 * <li>A class member to receive each parameter (command line, environment, property ).</li>
 * <li>A static setter method for each parameter.</li>
 * <li>An implementation of the class {@link CommandLineParameterDefinition} for each parameter to be processed, passed to
 * the super class constructor.</li>
 * <li>An Override of the super class {@link #Run} method to start the application logic.</li>
 * <li>A call to the super class {@link #go} method from the static main.</li>
 * </ul>
 *
 * <figure>
 * <div style="border: 1px solid; overflow: scroll;">
 * <pre>
 * class Sample extends AbstractApplication {
 * 
 *    private File inputFile;                     &#47;* Class member for input file command line parameter.       *&#47;
 *    private Long length;                        &#47;* Class member for length command line parameter.           *&#47;
 * 
 *    Sample() {
 *       super
 *          ( 
 *             "Sample",                          &#47;* Application short name for exception messages.            *&#47;
 *             "The Example Sample Application",  &#47;* Application descriptive name for help and usage messages. *&#47;
 *             "Application Description",         &#47;* A help / usage description for the Application.           *&#47;
 *             new CommandLineParameterFile       &#47;* Input file command line parameter definition.             *&#47;
 *                    (
 *                       "f",
 *                       "file",
 *                       true,
 *                       "Sample Input File",
 *                       true,
 *                       Sample::setInputFile
 *                    ),
 *             new CommandLineParameterLong       &#47;* Length command line parameter definition.                 *&#47;
 *                    (
 *                       "l",
 *                       "length",
 *                       false,
 *                       "The maximum length of input to process.",
 *                       Sample::setLength
 *                    )
 *          );
 *    }
 *    
 *    &#64;Override
 *    public void run() {                         &#47;* Start application logic in this method.                   *&#47;
 *    }
 *    
 *    &#47;* Static setter method for input file command line parameter value. *&#47;
 *    private setInputFile(Application sample, File inputFile) {  
 *       ((Sample) sample).inputFile = inputFile;
 *    }
 *    
 *    &#47;* Static setter method for length command line parameter value. *&#47;
 *    private setLength(Application sample, Long length) {
 *       ((Sample) sample).length = length;
 *    }
 *    
 *    &#47;* Application main method. &#47;*
 *    public static void main(String[] args) {
 *       try {
 *          &#47;* Create application class and invoke the go method. *&#47;
 *          new Sample().go( args ); 
 *       } catch( Exception e ) {
 *          &#47;* Print any unhandled exceptions. *&#47;
 *          System.err.append(e.getMessage()).append("\n);
 *          e.printStackTrace(System.err);
 *       }
 *    }
 * }
 * </pre>
 * </div>
 * <figcaption>AbstractApplication Sample Usage</figcaption>
 * </figure>
 * 
 * @author Loren K. Ashley
 */
//@formatter:on

package org.eclipse.ote.abstractapplication;