/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.ui.builder.impl;

import com.bc.ui.UIContextImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * @author Chinomso Bassey Ikwuagwu on May 10, 2018 11:44:19 AM
 */
public class Main {

    public static void main(String [] args) {
        
        final UIContextImpl uic = new UIContextImpl();
        try{

    //        uic.showProgressBar("10%", 0, 10, 100);
            uic.showProgressBarPercent(-1);

            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try(InputStream in = classLoader.getResourceAsStream("META-INF/logging.properties")) {

                LogManager.getLogManager().readConfiguration(in);

            }catch(IOException e) {
                e.printStackTrace();
            }
        

//        try{
//        for(int i=0; i < 7; i++) {
//            Thread.sleep(1000);
//            uic.showProgressBar((i * 20) + "%", 0, i * 20, 100);
//        } 
//        }catch(Throwable t) {
//            t.printStackTrace();
//        }

            final ReadMe readMe = new ReadMe();

            readMe.run();

        }catch(Throwable t) {
            t.printStackTrace();
        }finally{
            uic.showProgressBarPercent(100);
            System.exit(0);
        }
    }
}
