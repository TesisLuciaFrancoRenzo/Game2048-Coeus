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
package ar.edu.unrc.game2048.performanceandtraining.configurations.ntuples;

import ar.edu.unrc.coeus.tdlearning.training.ntuple.SamplePointValue;
import ar.edu.unrc.coeus.tdlearning.utils.FunctionUtils;
import ar.edu.unrc.game2048.GameBoard;
import ar.edu.unrc.game2048.NTupleConfiguration2048;
import ar.edu.unrc.game2048.Tile;

import java.util.ArrayList;

/**
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public
class NBasicLinearSimplified_512
        extends NTupleConfiguration2048 {

    /**
     * Configuración para jugar hasta 512, con función de activación Lineal, y puntaje parcial.
     */
    public
    NBasicLinearSimplified_512() {
        int numSamples = 8;
        int maxTile    = 9;

        activationFunction = FunctionUtils.LINEAR;
        derivedActivationFunction = FunctionUtils.LINEAR_DERIVED;
        concurrency = false;

        nTuplesLength = new int[numSamples];
        for (int i = 0; i < numSamples; i++) {
            nTuplesLength[i] = 4;
        }

        allSamplePointPossibleValues = new ArrayList<>();
        for (int spvIndex = 0; spvIndex <= maxTile; spvIndex++) {
            allSamplePointPossibleValues.add(new Tile(spvIndex));
        }
    }

    /**
     * @return @throws CloneNotSupportedException
     */
    @Override
    public
    Object clone()
            throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public
    double deNormalizeValueFromNeuralNetworkOutput(Object value) {
        return (double) value;
    }

    @Override
    public
    double getBoardReward(
            GameBoard board,
            int outputNeuron
    ) {
        return board.getPartialScore();
    }

    @Override
    public
    double getFinalReward(
            GameBoard board,
            int outputNeuron
    ) {
        return board.getGame().getScore();
    }

    @Override
    public
    SamplePointValue[] getNTuple(
            GameBoard board,
            int nTupleIndex
    ) {
        switch (nTupleIndex) {
            // verticales
            case 0: {
                return new SamplePointValue[]{board.tileAt(0, 0), board.tileAt(0, 1), board.tileAt(0, 2), board.tileAt(0, 3)};
            }
            case 1: {
                return new SamplePointValue[]{board.tileAt(1, 0), board.tileAt(1, 1), board.tileAt(1, 2), board.tileAt(1, 3)};
            }
            case 2: {
                return new SamplePointValue[]{board.tileAt(2, 0), board.tileAt(2, 1), board.tileAt(2, 2), board.tileAt(2, 3)};
            }
            case 3: {
                return new SamplePointValue[]{board.tileAt(3, 0), board.tileAt(3, 1), board.tileAt(3, 2), board.tileAt(3, 3)};
            }
            // horizontales
            case 4: {
                return new SamplePointValue[]{board.tileAt(0, 0), board.tileAt(1, 0), board.tileAt(2, 0), board.tileAt(3, 0)};
            }
            case 5: {
                return new SamplePointValue[]{board.tileAt(0, 1), board.tileAt(1, 1), board.tileAt(2, 1), board.tileAt(3, 1)};
            }
            case 6: {
                return new SamplePointValue[]{board.tileAt(0, 2), board.tileAt(1, 2), board.tileAt(2, 2), board.tileAt(3, 2)};
            }
            case 7: {
                return new SamplePointValue[]{board.tileAt(0, 3), board.tileAt(1, 3), board.tileAt(2, 3), board.tileAt(3, 3)};
            }
            default: {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
    }

    @Override
    public
    double normalizeValueToPerceptronOutput(Object value) {
        return (double) value;
    }

}
