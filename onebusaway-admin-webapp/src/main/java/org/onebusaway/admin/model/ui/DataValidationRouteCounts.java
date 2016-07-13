/**
 * Copyright (C) 2016 Cambridge Systematics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.admin.model.ui;

import java.util.List;

/**
 * Holds the stop count details for a particular route and particular mode.
 * All the trips for the route are checked for the number of stops they make.
 * This data is summarized by totaling the number of trips with a particular 
 * number of stops.
 *
 * @author jpearson
 *
 */
public class DataValidationRouteCounts {
  private String routeName;
  List<DataValidationStopCt> stopCounts;
  private String srcCode;  // Used in diff files to indicate the source.

  public String getRouteName() {
    return routeName;
  }
  public void setRouteName(String routeName) {
    this.routeName = routeName;
  }
  public List<DataValidationStopCt> getStopCounts() {
    return stopCounts;
  }
  public void setStopCounts(List<DataValidationStopCt> stopCounts) {
    this.stopCounts = stopCounts;
  }
  public String getSrcCode() {
    return srcCode;
  }
  public void setSrcCode(String srcCode) {
    this.srcCode = srcCode;
  }
}
