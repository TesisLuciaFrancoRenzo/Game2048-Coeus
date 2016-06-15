/*
 * Copyright (C) 2016  Lucia Bressan <lucyluz333@gmial.com>,
 *                     Franco Pellegrini <francogpellegrini@gmail.com>,
 *                     Renzo Bianchini <renzobianchini85@gmail.com
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.edu.unrc.game2048.performanceandtraining.experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author franc
 */
public class ArgumentLoader {

    /**
     *
     * @param args
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<Double> lambdaList = new ArrayList<>();
        lambdaList.add(0d);
        lambdaList.add(0.1d);
        lambdaList.add(0.2d);
        System.out.println(Arrays.toString(lambdaList.toArray(new Double[0])));
        //[0.0, 0.1, 0.2]

        String toLoad = "[0.0,0.1,0.2,5547]";
        System.out.println(parseDoubleArray(toLoad));
    }

    /**
     *
     * @param arrayString format like [0.0,0.1,0.2,5547]
     *
     * @return
     */
    public static List<Double> parseDoubleArray(String arrayString) {

        if ( arrayString == null || !arrayString.startsWith("[") || !arrayString.endsWith("]") ) {
            throw new IllegalArgumentException("arrayString format unknown: " + arrayString);
        }

        String[] list = arrayString.substring(1, arrayString.length() - 1).split(",");
        List<Double> out = new ArrayList<>(list.length);
        for ( String number : list ) {
            out.add(Double.parseDouble(number.trim()));
        }
        return out;
    }

    private Map<String, String> map;

    /**
     *
     * @param args
     */
    public ArgumentLoader(String[] args) {
        map = new HashMap<>(args.length);
        for ( String arg : args ) {
            int index = arg.indexOf('=');
            if ( index == -1 ) {
                throw new IllegalArgumentException("No se reconoce el argumento: " + arg);
            }
            String id = arg.substring(0, index).trim();
            String value = arg.substring(index + 1).trim();
            map.put(id, value);
        }
    }

    /**
     *
     * @param id
     *
     * @return
     */
    public String getArg(String id) {
        String value = map.get(id);
        if ( value == null ) {
            throw new IllegalArgumentException("No se reconoce el argumento: " + id);
        }
        return map.get(id);
    }

}
