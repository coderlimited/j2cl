/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.j2cl.ast.visitors;

import com.google.j2cl.ast.AbstractVisitor;
import com.google.j2cl.ast.CompilationUnit;
import com.google.j2cl.ast.Method;
import com.google.j2cl.ast.Statement;
import com.google.j2cl.common.SourcePosition;

/** Adds method qualified names to source positions for sourcemaps */
public class AddMethodNameToSourcePosition extends NormalizationPass {
  @Override
  public void applyTo(CompilationUnit compilationUnit) {
    compilationUnit.accept(
        new AbstractVisitor() {
          @Override
          public boolean enterStatement(Statement statement) {
            if (getCurrentMember() == null || !(getCurrentMember() instanceof Method)) {
              return false;
            }

            SourcePosition sourcePosition = statement.getSourcePosition();

            // If there is already a name in the AST do not overwrite
            // Some synthesized methods fill out the name earlier
            if (sourcePosition.getName() != null) {
              return true;
            }

            if (!sourcePosition.isAbsent()) {
              Method method = (Method) getCurrentMember();
              statement.setSourcePosition(
                  SourcePosition.Builder.from(sourcePosition)
                      .setName(method.getDescriptor().getQualifiedBinaryName())
                      .build());
            }
            return true;
          }
        });
  }
}
