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
package ar.edu.unrc.game2048.performanceandtraining.experiments.learning.ntuple;

import ar.edu.unrc.game2048.performanceandtraining.configurations.LearningExperiment;
import ar.edu.unrc.game2048.performanceandtraining.experiments.GeneratorConfig;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public class TestGeneratorActivationFunctionVsTraces {

    /**
     *
     * @param numberForShow
     * @param experiment
     * @param statisticsOnly
     * @param runStatisticsForBackups
     * @param createLogs
     * @param lambda
     * @param alpha
     * @param gamma
     * @param gamesToPlay
     * @param saveEvery
     * @param saveBacupEvery
     * @param gamesToPlayPerThreadForStatistics
     * @param simulationsForStatistics
     * @param explorationRate
     * @param resetEligibilitiTraces
     * @param filePath
     */
    public static void configAndExcecute(
            int numberForShow,
            LearningExperiment experiment,
            boolean statisticsOnly,
            boolean runStatisticsForBackups,
            boolean createLogs,
            double lambda,
            double alpha,
            double gamma,
            int gamesToPlay,
            int saveEvery,
            int saveBacupEvery,
            int gamesToPlayPerThreadForStatistics,
            int simulationsForStatistics,
            double explorationRate,
            boolean resetEligibilitiTraces,
            String filePath
    ) {
        experiment.setStatisticsOnly(statisticsOnly);
        experiment.setRunStatisticsForBackups(runStatisticsForBackups);
        experiment.createLogs(createLogs);
        experiment.setLambda(lambda);
        experiment.setGamma(gamma);
        double[] alphas = {alpha, alpha};
        experiment.setAlpha(alphas);
        experiment.setExplorationRateToFixed(explorationRate);
        experiment.setInitializePerceptronRandomized(false);
        experiment.setConcurrencyInComputeBestPosibleAction(true);
        boolean[] concurrentLayer = {false, false};
        experiment.setConcurrencyInLayer(concurrentLayer);
        experiment.setTileToWinForStatistics(2_048);
        experiment.setResetEligibilitiTraces(resetEligibilitiTraces);
        experiment.setLearningRateAdaptationToFixed();
        experiment.setGamesToPlay(gamesToPlay);
        experiment.setSaveEvery(saveEvery);
        experiment.setSaveBackupEvery(saveBacupEvery);
        experiment.setGamesToPlayPerThreadForStatistics(gamesToPlayPerThreadForStatistics);
        experiment.setSimulationsForStatistics(simulationsForStatistics);
        experiment.setExportToExcel(true);
        System.out.println("*=*=*=*=*=*=*=*=*=*=* N" + numberForShow + " Ejecutando " + filePath + " *=*=*=*=*=*=*=*=*=*=*");
        experiment.start(numberForShow, filePath, 0, true, null);
    }

    /**
     *
     * @param ex <p>
     * @return
     */
    public static String getMsj(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    /**
     *
     * @param args <p>
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String filePath
                = ".." + File.separator
                + "Perceptrones ENTRENADOS" + File.separator;
        List<Double> lambdaList = new ArrayList<>();
        List<Double> alphaList = new ArrayList<>();
        List<Double> gammaList = new ArrayList<>();
        List<Double> explorationRate = new ArrayList<>();

        //============================== configuraciones manuales ==================================
        int maxTrainingThreads = 8;
        int gamesToPlay = 20_000;
        int saveEvery = 1_000;
        int saveBackupEvery = 500;

        lambdaList.add(0d);
        lambdaList.add(0.1d);
        lambdaList.add(0.2d);
        lambdaList.add(0.6d);
        lambdaList.add(0.7d);
        lambdaList.add(0.8d);
        lambdaList.add(1d);

        alphaList.add(0.0025d);

        gammaList.add(1d);

        explorationRate.add(0d);

        boolean createLogs = false;
        //============================== fin de configuraciones manuales ==================================

        boolean statisticsOnly;
        boolean runStatisticsForBackups;
        int gamesToPlayPerThreadForStatistics;
        int simulationsForStatistics;

        statisticsOnly = false;
        runStatisticsForBackups = false;
        gamesToPlayPerThreadForStatistics = 0;
        simulationsForStatistics = 0;

        runAllConfigs(maxTrainingThreads, "BasicLinear_ActFuncVsTrace", BasicLinear.class.getConstructor(), alphaList, lambdaList, gammaList, statisticsOnly, runStatisticsForBackups, createLogs, gamesToPlay, saveEvery, saveBackupEvery, gamesToPlayPerThreadForStatistics, simulationsForStatistics, explorationRate, filePath);
        runAllConfigs(maxTrainingThreads, "BasicTanH_ActFuncVsTrace", BasicTanH.class.getConstructor(), alphaList, lambdaList, gammaList, statisticsOnly, runStatisticsForBackups, createLogs, gamesToPlay, saveEvery, saveBackupEvery, gamesToPlayPerThreadForStatistics, simulationsForStatistics, explorationRate, filePath);

        statisticsOnly = true;
        runStatisticsForBackups = true;
        gamesToPlayPerThreadForStatistics = 100;
        simulationsForStatistics = 8;

        runAllConfigs(maxTrainingThreads, "BasicLinear_ActFuncVsTrace", BasicLinear.class.getConstructor(), alphaList, lambdaList, gammaList, statisticsOnly, runStatisticsForBackups, createLogs, gamesToPlay, saveEvery, saveBackupEvery, gamesToPlayPerThreadForStatistics, simulationsForStatistics, explorationRate, filePath);
        runAllConfigs(maxTrainingThreads, "BasicTanH_ActFuncVsTrace", BasicTanH.class.getConstructor(), alphaList, lambdaList, gammaList, statisticsOnly, runStatisticsForBackups, createLogs, gamesToPlay, saveEvery, saveBackupEvery, gamesToPlayPerThreadForStatistics, simulationsForStatistics, explorationRate, filePath);
    }

    /**
     *
     * @param maxTrainingThreads
     * @param experimentName
     * @param experiment
     * @param alphaList
     * @param lambdaList
     * @param gammaList
     * @param statisticsOnly
     * @param runStatisticsForBackups
     * @param createLogs
     * @param gamesToPlay
     * @param saveEvery
     * @param saveBacupEvery
     * @param gamesToPlayPerThreadForStatistics
     * @param simulationsForStatistics
     * @param explorationRateList
     * @param filePath
     */
    public static void runAllConfigs(int maxTrainingThreads, String experimentName, Constructor<?> experiment, List<Double> alphaList, List<Double> lambdaList, List<Double> gammaList, boolean statisticsOnly, boolean runStatisticsForBackups, boolean createLogs, int gamesToPlay, int saveEvery, int saveBacupEvery, int gamesToPlayPerThreadForStatistics, int simulationsForStatistics, List<Double> explorationRateList, String filePath) {
        List<GeneratorConfig> experiments = new ArrayList<>();
        int number = 0;
        for ( int i = 0; i < alphaList.size(); i++ ) {
            for ( int j = 0; j < lambdaList.size(); j++ ) {
                for ( int k = 0; k < gammaList.size(); k++ ) {
                    for ( int l = 0; l < explorationRateList.size(); l++ ) {
                        number++;
                        experiments.add(new GeneratorConfig(alphaList.get(i), lambdaList.get(j), gammaList.get(k), explorationRateList.get(l), false, number));
                        if ( explorationRateList.get(l) > 0 && lambdaList.get(j) > 0 ) {
                            number++;
                            experiments.add(new GeneratorConfig(alphaList.get(i), lambdaList.get(j), gammaList.get(k), explorationRateList.get(l), true, number));
                        }
                    }
                }
            }
        }

        Stream<GeneratorConfig> stream;
        if ( statisticsOnly ) {
            stream = experiments.stream();
        } else {
            stream = experiments.parallelStream();
        }

        ForkJoinPool forkJoinPool = new ForkJoinPool(maxTrainingThreads);
        try {
            forkJoinPool.submit(()
                    -> //parallel task here, for example
                    stream.forEach(expConfig -> {
                        try {
                            String newFilePath = filePath + "ActivationFunctionVsTraces" + File.separator + "alpha_" + expConfig.getAlpha() + "-lambda_" + expConfig.getLambda() + "-gamma_" + expConfig.getGamma() + "-explorationRate_" + expConfig.getExplorationRate() + "-resetTraces_" + expConfig.isResetTraces() + File.separator;
                            File newPath = new File(newFilePath);
                            if ( !newPath.exists() ) {
                                newPath.mkdirs();
                            }
                            LearningExperiment cloneExperiment = (LearningExperiment) experiment.newInstance();
                            cloneExperiment.setExperimentName(experimentName);
                            configAndExcecute(expConfig.getNumber(), cloneExperiment, statisticsOnly, runStatisticsForBackups, createLogs, expConfig.getLambda(), expConfig.getAlpha(), expConfig.getGamma(), gamesToPlay, saveEvery, saveBacupEvery, gamesToPlayPerThreadForStatistics, simulationsForStatistics, expConfig.getExplorationRate(), expConfig.isResetTraces(), newFilePath);
                        } catch ( InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex ) {
                            Logger.getLogger(TestGeneratorActivationFunctionVsTraces.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    })
            ).get();
        } catch ( InterruptedException | ExecutionException ex ) {
            Logger.getLogger(TestGeneratorActivationFunctionVsTraces.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}