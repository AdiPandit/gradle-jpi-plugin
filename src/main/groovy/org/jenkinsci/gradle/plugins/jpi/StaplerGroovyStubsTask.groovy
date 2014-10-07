/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenkinsci.gradle.plugins.jpi

import org.gradle.api.DefaultTask
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputDirectory

/**
 * Generates Java stubs from Groovy source, in order to be processed by javac for JSR-269 annotations.
 *
 * @author Andrew Bayer
 */
class StaplerGroovyStubsTask extends DefaultTask {
  @OutputDirectory
  File destinationDir

  @TaskAction
  def generateStubs() {
    def p = project
    def isolatedAnt = services.get(IsolatedAntBuilder)
    isolatedAnt.execute {
      mkdir(dir: destinationDir.canonicalPath)
      taskdef(name: 'generatestubs', classname: 'org.codehaus.groovy.ant.GenerateStubsTask') {
        classpath {
        pathelement path: p.sourceSets.main.compileClasspath.asPath
        }
      }

      generatestubs(destdir: destinationDir.canonicalPath) {
        configuration(targetByteCode: '1.6')
        classpath {
        pathelement path: p.sourceSets.main.compileClasspath.asPath
        }
        src {
          p.sourceSets.main.groovy.srcDirs.each { srcDir ->
            if (srcDir.exists()) {
              dirset(dir: srcDir) {
              exclude name: '**/*.properties'
              }
            }
          }
        }
      }
    }
  }

  public static final String TASK_NAME = 'stapler'
}
