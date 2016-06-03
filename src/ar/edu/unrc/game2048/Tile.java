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
package ar.edu.unrc.game2048;

import ar.edu.unrc.tdlearning.training.ntuple.SamplePointState;
import java.awt.Color;
import static java.lang.Math.pow;

/**
 * Representacion de una posicion en el tablero
 * <p>
 * @author lucia bressan, franco pellegrini, renzo bianchini pellegrini
 */
public class Tile implements SamplePointState {

    private int code;
    private int gameValue;

    /**
     *
     */
    public Tile() {
        this(0);
    }

    /**
     *
     * @param num
     */
    public Tile(int num) {
        code = num;
        if ( code == 0 ) {
            gameValue = 0;
        } else {
            gameValue = (int) pow(2, code);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Tile other = (Tile) obj;
        return this.code == other.code;
    }

    /**
     *
     * @return
     */
    public Color getBackground() {
        switch ( code ) {
            case 1:
                return new Color(0xee_e4da);
            case 2:
                return new Color(0xed_e0c8);
            case 3:
                return new Color(0xf2_b179);
            case 4: //16
                return new Color(0xf5_9563);
            case 5:
                return new Color(0xf6_7c5f);
            case 6:
                return new Color(0xf6_5e3b);
            case 7:
                return new Color(0xed_cf72);
            case 8:
                return new Color(0xed_cc61);
            case 9:
                return new Color(0xed_c850);
            case 10:
                return new Color(0xed_c53f);
            case 11:
                return new Color(0xed_c22e);
        }
        return new Color(0xcd_c1b4);
    }

    /**
     *
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     *
     * @return
     */
    public Color getForeground() {
        return code < 4 ? new Color(0x77_6e65) : new Color(0xf9_f6f2);
    }

    /**
     *
     * @return
     */
    public int getGameValue() {
        return gameValue;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.code;
        return hash;
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return code == 0;
    }

}
