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
package ar.edu.unrc.game2048.experiments.configurations.ntuples;

import ar.edu.unrc.coeus.tdlearning.training.ntuple.SamplePointValue;
import ar.edu.unrc.coeus.utils.FunctionUtils;
import ar.edu.unrc.game2048.GameBoard;
import ar.edu.unrc.game2048.Tile;
import ar.edu.unrc.game2048.experiments.configurations.NTupleConfiguration2048;

import java.util.ArrayList;

/**
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public
class ConfigNTupleSymmetricLinear_32768
        extends NTupleConfiguration2048 {

    /**
     * Configuración para jugar hasta 32.768 con tablero simétrico, con función de activación Lineal, y puntaje parcial.
     */
    public
    ConfigNTupleSymmetricLinear_32768() {
        super();
        setTileToWinForTraining(32768);

        activationFunction = FunctionUtils.LINEAR;
        derivedActivationFunction = FunctionUtils.LINEAR_DERIVED;
        concurrency = false;
        final int maxTile = 15;

        nTuplesLength = new int[4];
        nTuplesLength[0] = 6;
        nTuplesLength[1] = 6;
        nTuplesLength[2] = 4;
        nTuplesLength[3] = 4;

        allSamplePointPossibleValues = new ArrayList<>();
        allSamplePointPossibleValues.add(null);
        for ( int i = 1; i <= maxTile; i++ ) {
            allSamplePointPossibleValues.add(new Tile((int) Math.pow(2, i)));
        }
    }

    @Override
    public
    ConfigNTupleSymmetricLinear_32768 clone()
            throws CloneNotSupportedException {
        return (ConfigNTupleSymmetricLinear_32768) super.clone();
    }

    @Override
    public
    double deNormalizeValueFromNeuralNetworkOutput( final Object value ) {
        return (double) value;
    }

    @Override
    public
    SamplePointValue[] getNTuple(
            final GameBoard board,
            final int nTupleIndex
    ) {
        final Tile[][] tiles = board.getTiles();
        switch ( nTupleIndex ) {
            // rectángulos
            case 0:
                return new SamplePointValue[] { tiles[0][0], tiles[0][1], tiles[0][2], tiles[1][0], tiles[1][1], tiles[1][2] };
            case 1:
                return new SamplePointValue[] { tiles[1][0], tiles[1][1], tiles[1][2], tiles[2][0], tiles[2][1], tiles[2][2] };
            // verticales
            case 2:
                return new SamplePointValue[] { tiles[2][0], tiles[2][1], tiles[2][2], tiles[2][3] };
            case 3:
                return new SamplePointValue[] { tiles[3][0], tiles[3][1], tiles[3][2], tiles[3][3] };
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public
    double normalizeValueToPerceptronOutput( final Object value ) {
        return (double) value;
    }
}
