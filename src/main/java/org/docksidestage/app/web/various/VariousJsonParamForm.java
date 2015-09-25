/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.app.web.various;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.docksidestage.dbflute.allcommon.CDef;
import org.lastaflute.web.api.JsonParameter;

/**
 * @author jflute
 */
public class VariousJsonParamForm {

    @JsonParameter
    public CheckJsonParam param;

    @JsonParameter
    public List<CheckJsonParam> listParam;

    public static class CheckJsonParam {

        public Integer first;
        public String second;
        public LocalDate third;
        public LocalDateTime fourth;
        public BigDecimal fifth;
        public List<CheckJsonParam> sixth;
        public CheckJsonParam seventh;
        public CDef.MemberStatus eighth;

        @Override
        public String toString() {
            return getClass() + ":{" + first + ", " + second + ", " + third + ", " + fourth + ", " + fifth + ", " + sixth + ", " + seventh
                    + ", " + eighth + "}";
        }
    }
}
