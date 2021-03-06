//  Gant -- A Groovy way of scripting Ant tasks.
//
//  Copyright © 2006–2008, 2010, 2013  Russel Winder
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License is
//  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//  implied. See the License for the specific language governing permissions and limitations under the
//  License.

package gant.targets

import org.codehaus.gant.GantBinding

/**
 *  A class to provide clean and clobber actions for Gant build scripts.  Maintains separate lists of
 *  Ant pattern specifications and directory names for clean and for clobber.  The lists are used as the
 *  specifications when the clean or clobber methods are called.
 *
 *  @author Russel Winder <russel@winder.org.uk>
 */
final class Clean {
  private GantBinding binding
  private performPatternAction(final List<String> l) {
    if (l.size() > 0) {
      binding.ant.delete(quiet: 'false') {
        binding.ant.fileset(dir: '.', includes: l.flatten().join(', '), defaultexcludes: 'false')
      }
    }
  }
  private performDirectoryAction(final List<String> l) {
    l.flatten().each{item -> binding.ant.delete(dir: item, quiet: 'false')}
  }
  /**
   *  Constructor for the "includeTargets <<" usage.
   *
   *  @param binding The <code>GantBinding</code> to bind to.
   */
 Clean(final GantBinding binding) {
    this.binding = binding
    binding.cleanPattern = []
    binding.cleanDirectory = []
    binding.target.call(clean: 'Action the cleaning.') {
      performPatternAction(binding.cleanPattern)
      performDirectoryAction(binding.cleanDirectory)
    }
    binding.clobberPattern = []
    binding.clobberDirectory = []
    binding.target.call(clobber: 'Action the clobbering. Do the cleaning first.') {
      depends(binding.clean)
      performPatternAction(binding.clobberPattern)
      performDirectoryAction(binding.clobberDirectory)
    }
  }
  /**
   *  Constructor for the "includeTargets **" usage.  Currently ignores keys other than cleanPattern,
   *  cleanDirectory, clobberPattern, and clobberDirectory.
   *
   *  @param binding The <code>GantBinding</code> to bind to.
   *  @param map The <code>Map</code> of initialization parameters.
   */
  Clean(final GantBinding binding , final Map<String,String> map) {
    this(binding)
    map.each{key , value ->
      if (['cleanPattern', 'cleanDirectory', 'clobberPattern', 'clobberDirectory'].contains(key)) {
        binding."${key}" << value
      }
    }
  }
}
