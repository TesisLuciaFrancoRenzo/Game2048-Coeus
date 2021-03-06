package ar.edu.unrc.game2048.experiments.configurations;

import ar.edu.unrc.coeus.interfaces.INeuralNetworkInterface;
import ar.edu.unrc.coeus.tdlearning.learning.TDLambdaLearning;
import ar.edu.unrc.game2048.Game2048;
import ar.edu.unrc.game2048.Tile;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static java.lang.Math.round;

/**
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public abstract
class StatisticExperiment {

    /**
     *
     */
    public static final String MAX_SCORE = "Maximo puntaje: ";

    /**
     *
     */
    public static final String MAX_TURN = "Maximo turno: ";

    /**
     *
     */
    public static final String MEAN_SCORE = "Media puntaje: ";

    /**
     *
     */
    public static final String MEAN_TURN = "Media turno: ";

    /**
     *
     */
    public static final String MIN_SCORE = "Mínimo puntaje: ";

    /**
     *
     */
    public static final String MIN_TURN = "Mínimo turno: ";

    /**
     *
     */
    public static final String WIN_RATE = "Win rate: ";
    /**
     * Experimento de aprendizaje.
     */
    protected final LearningExperiment learningExperiment;
    private boolean          backupStatisticOnly     = false;
    private String           experimentName          = null;
    private boolean          exportToExcel           = true;
    private String           fileName                = null;
    private int              gamesToPlay             = 0;
    private TDLambdaLearning learningMethod          = null;
    private double           maxScore                = 0.0;
    private double           maxTurn                 = 0.0;
    private double           meanScore               = 0.0;
    private double           meanTurn                = 0.0;
    private double           minScore                = 0.0;
    private double           minTurn                 = 0.0;
    private boolean          runStatisticsForBackups = false;
    private int              saveBackupEvery         = 0;
    private int              simulations             = 0;
    private List< Double >   tileStatistics          = null;
    private int              tileToWin               = 0;
    private int              tileToWinForStatistics  = 2_048;
    private double           winRate                 = 0.0;

    /**
     * @param learningExperiment Experimento de aprendizaje.
     */
    protected
    StatisticExperiment(
            final LearningExperiment learningExperiment
    ) {
        super();
        this.learningExperiment = learningExperiment;
    }

    /**
     * Exporta a una hoja de cálculo los resultados de las estadísticas ya calculadas en archivos de texto.
     *
     * @param filePath       Ruta de donde se exporta la hoja de cálculo.
     * @param backupFiles    Lista de los archivos con las redes neuronales que se le realizaron estadísticas.
     * @param resultsPerFile Mapeo de los archivos asociados a los resultados parseados.
     * @param resultsRandom  Mapeo de los archivos asociados a los resultados parseados de la red sin entrenamiento.
     *
     * @throws IOException            al escribir archivos.
     * @throws InvalidFormatException en formatos.
     */
    public
    void exportToExcel(
            final String filePath,
            final List< File > backupFiles,
            final Map< File, StatisticForCalc > resultsPerFile,
            final StatisticForCalc resultsRandom
    )
            throws IOException, InvalidFormatException {
        final InputStream inputXLSX = getClass().getResourceAsStream("/ar/edu/unrc/game2048/experiments/Estadisticas.xlsx");
        try ( Workbook wb = WorkbookFactory.create(inputXLSX) ) {

            try ( FileOutputStream outputXLSX = new FileOutputStream(filePath + "_STATISTICS" + ".xlsx") ) {
                //============= imprimimos en la hoja de tiles ===================

                Sheet sheet = wb.getSheetAt(0);
                //Estilo par los títulos de las tablas
                // Luego creamos el objeto que se encargará de aplicar el estilo a la celda
                final Font fontCellTitle = wb.createFont();
                fontCellTitle.setFontHeightInPoints((short) 10);
                fontCellTitle.setFontName("Arial");
                fontCellTitle.setBold(true);
                final CellStyle CellStyleTitle = wb.createCellStyle();
                CellStyleTitle.setWrapText(true);
                CellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
                CellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
                CellStyleTitle.setFont(fontCellTitle);

                // Establecemos el tipo de sombreado de nuestra celda
                CellStyleTitle.setFillBackgroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
                CellStyleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                final int colStartTitle = 2;
                final int rowStartTitle = 0;
                loadTitle(rowStartTitle, colStartTitle, sheet, backupFiles.size(), CellStyleTitle);
                //estilo titulo finalizado

                //Estilo de celdas con los valores de las estadisticas
                final CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setWrapText(true);
                // We are now ready to set borders for this style. Draw a thin left border
                cellStyle.setBorderLeft(BorderStyle.THIN);
                // Add medium right border
                cellStyle.setBorderRight(BorderStyle.THIN);
                // Add dashed top border
                cellStyle.setBorderTop(BorderStyle.THIN);
                // Add dotted bottom border
                cellStyle.setBorderBottom(BorderStyle.THIN);
                //estilo celdas finalizado

                //configuraciones basadas en el spreadsheet
                Cell      cell;
                Row       row;
                Double    cellDoubleValue;
                int       rowStart = 2;
                final int colStart = 3;
                final int tiles    = 17;
                for ( int tile = 0; tile <= tiles; tile++ ) {
                    row = sheet.getRow(( tile + rowStart ) - 1);
                    for ( int file = 0; file < backupFiles.size(); file++ ) {
                        cell = row.createCell(file + colStart, CellType.NUMERIC);
                        cell.setCellStyle(cellStyle);
                        cellDoubleValue = resultsPerFile.get(backupFiles.get(file)).getTileStatistics().get(tile);
                        cell.setCellValue(cellDoubleValue);
                    }
                }
                if ( resultsRandom != null ) {
                    for ( int tile = 0; tile <= tiles; tile++ ) {
                        row = sheet.getRow(( tile + rowStart ) - 1);
                        final int file = 0;
                        cell = row.createCell(( file + colStart ) - 1, CellType.NUMERIC);
                        cellDoubleValue = resultsRandom.getTileStatistics().get(tile);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(cellDoubleValue);
                    }
                }

                //============= imprimimos en la hoja de Score ===================
                sheet = wb.getSheetAt(1);
                rowStart = 2;
                loadTitle(rowStartTitle, colStartTitle, sheet, backupFiles.size(), CellStyleTitle);
                row = sheet.getRow(rowStart - 1);
                for ( int file = 0; file < backupFiles.size(); file++ ) {
                    cell = row.createCell(file + colStart, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsPerFile.get(backupFiles.get(file)).getMinScore();
                    cell.setCellValue(cellDoubleValue);
                }
                if ( resultsRandom != null ) {
                    final int file = 0;
                    cell = row.createCell(( file + colStart ) - 1, CellType.NUMERIC);
                    cellDoubleValue = resultsRandom.getMinScore();
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(cellDoubleValue);
                }

                rowStart = 3;
                row = sheet.getRow(rowStart - 1);
                for ( int file = 0; file < backupFiles.size(); file++ ) {
                    cell = row.createCell(file + colStart, CellType.NUMERIC);
                    cellDoubleValue = resultsPerFile.get(backupFiles.get(file)).getMeanScore();
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(cellDoubleValue);
                }
                if ( resultsRandom != null ) {
                    final int file = 0;
                    cell = row.createCell(( file + colStart ) - 1, CellType.NUMERIC);
                    cellDoubleValue = resultsRandom.getMeanScore();
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(cellDoubleValue);
                }

                rowStart = 4;
                row = sheet.getRow(rowStart - 1);
                for ( int file = 0; file < backupFiles.size(); file++ ) {
                    cell = row.createCell(file + colStart, CellType.NUMERIC);
                    cellDoubleValue = resultsPerFile.get(backupFiles.get(file)).getMaxScore();
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(cellDoubleValue);
                }
                if ( resultsRandom != null ) {
                    final int file = 0;
                    cell = row.createCell(( file + colStart ) - 1, CellType.NUMERIC);
                    cellDoubleValue = resultsRandom.getMaxScore();
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(cellDoubleValue);
                }

                //============= imprimimos en la hoja de Win ===================
                sheet = wb.getSheetAt(2);
                sheet.setForceFormulaRecalculation(true);
                rowStart = 2;
                loadTitle(rowStartTitle, colStartTitle, sheet, backupFiles.size(), CellStyleTitle);
                loadTitle(rowStartTitle + 2, colStartTitle, sheet, backupFiles.size(), CellStyleTitle);
                row = sheet.getRow(rowStart - 1);
                for ( int file = 0; file < backupFiles.size(); file++ ) {
                    cell = row.createCell(file + colStart, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsPerFile.get(backupFiles.get(file)).getWinRate();
                    assert ( cellDoubleValue <= 100 ) && ( cellDoubleValue >= 0 );
                    cell.setCellValue(cellDoubleValue);
                }
                if ( resultsRandom != null ) {
                    final int file = 0;
                    cell = row.createCell(( file + colStart ) - 1, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsRandom.getWinRate();
                    assert ( cellDoubleValue <= 100 ) && ( cellDoubleValue >= 0 );
                    cell.setCellValue(cellDoubleValue);
                }
                // creamos la celda para calcular máxima/mejor red entrenada
                row = sheet.getRow(3);
                cell = row.createCell(2, CellType.FORMULA);
                cell.setCellStyle(cellStyle);
                final int    maxCol       = ( backupFiles.size() + colStart ) - 1;
                final String columnLetter = CellReference.convertNumToColString(maxCol);
                String       formula      = "MAX(C2:" + columnLetter + "2)";
                cell.setCellFormula(formula);

                cell = row.createCell(3, CellType.FORMULA);
                cell.setCellStyle(cellStyle);
                formula = "HLOOKUP(C4,C2:" + columnLetter + "3,2)";
                cell.setCellFormula(formula);
                //============= imprimimos en la hoja de Turns ===================
                sheet = wb.getSheetAt(3);
                rowStart = 2;
                loadTitle(rowStartTitle, colStartTitle, sheet, backupFiles.size(), CellStyleTitle);
                row = sheet.getRow(rowStart - 1);
                for ( int file = 0; file < backupFiles.size(); file++ ) {
                    cell = row.createCell(file + colStart, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsPerFile.get(backupFiles.get(file)).getMinTurn();
                    cell.setCellValue(cellDoubleValue);
                }
                if ( resultsRandom != null ) {
                    final int file = 0;
                    cell = row.createCell(( file + colStart ) - 1, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsRandom.getMinTurn();
                    cell.setCellValue(cellDoubleValue);
                }

                rowStart = 3;
                row = sheet.getRow(rowStart - 1);
                for ( int file = 0; file < backupFiles.size(); file++ ) {
                    cell = row.createCell(file + colStart, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsPerFile.get(backupFiles.get(file)).getMeanTurn();
                    cell.setCellValue(cellDoubleValue);
                }
                if ( resultsRandom != null ) {
                    final int file = 0;
                    cell = row.createCell(( file + colStart ) - 1, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsRandom.getMeanTurn();
                    cell.setCellValue(cellDoubleValue);
                }

                rowStart = 4;
                row = sheet.getRow(rowStart - 1);
                for ( int file = 0; file < backupFiles.size(); file++ ) {
                    cell = row.createCell(file + colStart, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsPerFile.get(backupFiles.get(file)).getMaxTurn();
                    cell.setCellValue(cellDoubleValue);
                }
                if ( resultsRandom != null ) {
                    final int file = 0;
                    cell = row.createCell(( file + colStart ) - 1, CellType.NUMERIC);
                    cell.setCellStyle(cellStyle);
                    cellDoubleValue = resultsRandom.getMaxTurn();
                    cell.setCellValue(cellDoubleValue);
                }

                wb.write(outputXLSX);
            }
        }
    }

    private
    double extractNumber( final String line ) {
        final int index = line.indexOf(':');
        assert index != -1;
        return Double.parseDouble(line.substring(index + 1).trim().replaceFirst(",", "."));
    }

    /**
     * @return las estadísticas listas para ser exportadas en la hoja de cálculo.
     */
    public
    StatisticForCalc getTileStatistics() {
        final StatisticForCalc statistic = new StatisticForCalc();
        statistic.setWinRate(winRate);
        statistic.setMaxScore(maxScore);
        statistic.setMeanScore(meanScore);
        statistic.setMinScore(minScore);
        statistic.setMaxTurn(maxTurn);
        statistic.setMeanTurn(meanTurn);
        statistic.setMinTurn(minTurn);
        statistic.setTileStatistics(tileStatistics);
        return statistic;
    }

    public
    double getWinRate() {
        return winRate;
    }

    /**
     * Se deben inicializar: <ul> <li>private int delayPerMove;</li> <li>private IPlayingExperiment playingExperiment;</li> <li>private String
     * fileName;</li> </ul> Las siguientes variables se inicializan automáticamente, pero pueden ser modificadas: <ul> <li>private int tileToWin;</li>
     * <li>private String experimentName;</li> <li>private EncogConfiguration2048 perceptronConfiguration;</li> <li>private TDLambdaLearning
     * learningMethod;</li> </ul>
     */
    protected abstract
    void initializeStatistics();

    /**
     * Configura las cabeceras de las tablas en la hoja de cálculo.
     *
     * @param rowStartTitle   fila de inicio de la tabla.
     * @param colStartTitle   columna de inicio de la tabla.
     * @param sheet           hoja de cálculo.
     * @param backupFilesSize cantidad de archivos de respaldo realizados.
     * @param CellStyleTitle  estilo de la celda.
     */
    public
    void loadTitle(
            final int rowStartTitle,
            final int colStartTitle,
            final Sheet sheet,
            final int backupFilesSize,
            final CellStyle CellStyleTitle
    ) {
        final int totalGames = saveBackupEvery;
        final Row row1       = sheet.getRow(rowStartTitle);
        for ( int file = 1; file <= backupFilesSize; file++ ) {
            final Cell cell = row1.createCell(file + colStartTitle, CellType.NUMERIC);
            cell.setCellStyle(CellStyleTitle);
            final Integer value    = totalGames * file;
            final String  valueStr = value.toString();
            String        cellV    = valueStr;
            if ( valueStr.length() > 3 ) {
                cellV = valueStr.substring(0, valueStr.length() - 3) + 'K';
            }
            cell.setCellValue(cellV);
        }
    }

    /**
     * Calcula las estadísticas de una red neuronal.
     *
     * @param fileToProcess           archivo a procesar.
     * @param createNeuralNetworkFile true si debe crear los perceptrones faltantes.
     *
     * @throws Exception al leer o escribir un archivo de configuración o estadística
     */
    public
    void processFile(
            final String fileToProcess,
            final boolean createNeuralNetworkFile,
            final boolean printHistory
    )
            throws Exception {

        //preparamos los destinos de las simulaciones para posterior sumatoria final
        final File logFile = new File(fileToProcess + "_STATISTICS" + ".txt");

        if ( logFile.exists() ) {
            //cargamos el archivo ya guardado
            try ( BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8")) ) {
                tileStatistics = new ArrayList<>(18);
                int lastTileStatistic = -1;
                for ( String line = br.readLine(); line != null; line = br.readLine() ) {
                    if ( line.contains(WIN_RATE) ) {
                        winRate = extractNumber(line);
                    } else if ( line.contains(MIN_TURN) ) {
                        minTurn = extractNumber(line);
                    } else if ( line.contains(MEAN_TURN) ) {
                        meanTurn = extractNumber(line);
                    } else if ( line.contains(MAX_TURN) ) {
                        maxTurn = extractNumber(line);
                    } else if ( line.contains(MIN_SCORE) ) {
                        minScore = extractNumber(line);
                    } else if ( line.contains(MEAN_SCORE) ) {
                        meanScore = extractNumber(line);
                    } else if ( line.contains(MAX_SCORE) ) {
                        maxScore = extractNumber(line);
                    } else {
                        try {
                            final double value = Double.parseDouble(line.trim().replaceFirst(",", "."));
                            lastTileStatistic++;
                            tileStatistics.add(value);
                        } catch ( final NumberFormatException ignored ) {
                        }
                    }

                }
                assert lastTileStatistic == 17;
            }
            System.out.println("Finished.");
        } else {
            final List< ThreadResult >                   results                 = new ArrayList<>(simulations);
            final List< Game2048 >                       games                   = new ArrayList<>(simulations);
            final List< INeuralNetworkInterfaceFor2048 > neuralNetworkInterfaces = new ArrayList<>(simulations);
            final List< TDLambdaLearning >               tdLambdaLearning        = new ArrayList<>(simulations);

            for ( int i = 0; i < simulations; i++ ) {
                final INeuralNetworkInterfaceFor2048 neuralNetworkInterfaceClone = learningExperiment.getNeuralNetworkInterfaceFor2048().clone();

                INeuralNetworkInterface tempPerceptronInterface = null;

                EncogConfiguration2048 tempPerceptronConfiguration = null;
                if ( learningExperiment.getNeuralNetworkInterfaceFor2048().getPerceptronConfiguration() != null ) {
                    tempPerceptronConfiguration = learningExperiment.getNeuralNetworkInterfaceFor2048().getPerceptronConfiguration().clone();
                    neuralNetworkInterfaceClone.setPerceptronConfiguration(tempPerceptronConfiguration);
                    tempPerceptronInterface = neuralNetworkInterfaceClone.getNeuralNetworkInterface();
                }
                NTupleConfiguration2048 tempNTupleConfiguration = null;
                if ( learningExperiment.getNeuralNetworkInterfaceFor2048().getNTupleConfiguration() != null ) {
                    tempNTupleConfiguration = learningExperiment.getNeuralNetworkInterfaceFor2048().getNTupleConfiguration().clone();
                    neuralNetworkInterfaceClone.setNTupleConfiguration(tempNTupleConfiguration);
                }

                if ( ( tempPerceptronConfiguration != null ) || ( tempNTupleConfiguration != null ) ) {
                    //cargamos la red neuronal entrenada
                    final File perceptronFile = new File(fileToProcess + ".ser");
                    if ( !perceptronFile.exists() ) {
                        throw new IllegalArgumentException("perceptron file must exists: " + perceptronFile.getCanonicalPath());
                    }
                    neuralNetworkInterfaceClone.loadOrCreatePerceptron(perceptronFile, true, createNeuralNetworkFile);
                }

                final Game2048 game = new Game2048(tempPerceptronConfiguration, tempNTupleConfiguration, tileToWinForStatistics, printHistory);

                neuralNetworkInterfaces.add(neuralNetworkInterfaceClone);
                if ( tempPerceptronConfiguration != null ) {
                    tdLambdaLearning.add(learningExperiment.instanceOfTdLearningImplementation(tempPerceptronInterface));
                }
                if ( tempNTupleConfiguration != null ) {
                    tdLambdaLearning.add(learningExperiment.instanceOfTdLearningImplementation(tempNTupleConfiguration.getNTupleSystem()));
                }
                games.add(game);
                results.add(new ThreadResult());
            }

            IntStream.range(0, simulations).parallel().forEach(i -> {
                // Si hay un perceptron ya entrenado, lo buscamos en el archivo.
                // En caso contrario creamos un perceptron vacío, inicializado al azar
                final ThreadResult threadResult = results.get(i);
                for ( threadResult.setProcessedGames(1); threadResult.getProcessedGames() < gamesToPlay; threadResult.addProcessedGames() ) {
                    final Game2048 game2048 = games.get(i);
                    game2048.initialize(); //reset
                    int turnNumber = 0;
                    while ( game2048.isRunning() ) {
                        if ( tdLambdaLearning.isEmpty() ) {
                            neuralNetworkInterfaces.get(i).playATurn(game2048, null);
                        } else {
                            neuralNetworkInterfaces.get(i).playATurn(game2048, tdLambdaLearning.get(i));
                        }
                        turnNumber++;
                    }
                    //calculamos estadisticas
                    threadResult.addStatisticForTile(Tile.getCodeFromTileValue(game2048.getMaxNumber()));
                    threadResult.addScore(game2048.getScore());

                    if ( game2048.getMaxNumber() >= tileToWinForStatistics ) {
                        threadResult.addWin();
                        threadResult.addLastTurn(turnNumber);
                    }
                    //                    if ( threadResult.getProcessedGames() % 100 == 0 ) {
                    //                        System.out.println("Thread " + i + " -> Ultimo resultado = " + game2048.getMaxNumber() + "
                    // turnNumber=" + turnNumber + " - " +
                    //                                           threadResult.getProcessedGames() + "/" + gamesToPlay);
                    //                    }
                }
            });

            winRate = 0;
            maxScore = 0;
            minScore = 0;
            meanScore = 0;
            maxTurn = 0;
            minTurn = 0;
            meanTurn = 0;

            tileStatistics = new ArrayList<>(18);
            for ( int i = 0; i <= 17; i++ ) {
                tileStatistics.add(0.0d);
            }
            results.forEach(( result ) -> {
                winRate += result.getWinRate();
                maxScore += result.getMaxScore();
                minScore += result.getMinScore();
                meanScore += result.getMeanScore();
                maxTurn += result.getMaxTurn();
                minTurn += result.getMinTurn();
                meanTurn += result.getMeanTurn();
                for ( int i = 0; i <= 17; i++ ) {
                    tileStatistics.set(i, tileStatistics.get(i) + result.getStatisticForTile(i));
                }
            });

            for ( int i = 0; i <= 17; i++ ) {
                tileStatistics.set(i, tileStatistics.get(i) / ( simulations * 1.0d ));
            }
            winRate /= ( simulations * 1.0d );
            assert winRate <= 100;
            maxScore /= ( simulations * 1.0d );
            minScore /= ( simulations * 1.0d );
            meanScore /= ( simulations * 1.0d );
            maxTurn /= ( simulations * 1.0d );
            minTurn /= ( simulations * 1.0d );
            meanTurn /= ( simulations * 1.0d );

            if ( !results.isEmpty() ) {
                try ( PrintStream printStream = new PrintStream(logFile, "UTF-8") ) {
                    printStream.println(
                            "Gano: " + round(winRate) + "% - Total de partidas: " + gamesToPlay + " (promedios obtenidos con " + simulations +
                            " simulaciones)");
                    printStream.println("Valores alcanzados:");
                    for ( int i = 0; i <= 17; i++ ) {
                        printStream.println(tileStatistics.get(i).toString().replaceAll("\\.", ","));
                    }

                    printStream.println(MAX_SCORE + maxScore);
                    printStream.println(MEAN_SCORE + meanScore);
                    printStream.println(MIN_SCORE + minScore);

                    printStream.println(MAX_TURN + maxTurn);
                    printStream.println(MEAN_TURN + meanTurn);
                    printStream.println(MIN_TURN + minTurn);

                    printStream.println(WIN_RATE + winRate);
                }
                System.out.println("Finished.");
            }
        }
    }

    /**
     * Inicia las estadísticas.
     *
     * @param experimentPath          directorio de la red neuronal.
     * @param createNeuralNetworkFile crea redes neuronales en caso de no existir.
     *
     * @throws Exception al ejecutar el experimento.
     */
    protected
    void run(
            final String experimentPath,
            final boolean createNeuralNetworkFile,
            final boolean printHistory
    )
            throws Exception {
        final String dirPath = experimentPath + learningExperiment.getNeuralNetworkInterfaceFor2048().getLibName() + File.separator + experimentName +
                               File.separator;
        final File dirPathFile = new File(dirPath);
        if ( !dirPathFile.exists() ) {
            dirPathFile.mkdirs();
        }
        final String filePath = dirPath + fileName;

        //hacemos estadisticas del perceptron random, si es necesario
        System.out.print("Starting " + experimentName + LearningExperiment.RANDOM + " Statistics... ");
        processFile(dirPath + experimentName + LearningExperiment.RANDOM, createNeuralNetworkFile, printHistory);
        final StatisticForCalc resultsRandom = getTileStatistics();

        if ( !backupStatisticOnly ) {
            File bestFile = new File(dirPath + experimentName + LearningExperiment.BEST_TRAINED);
            if ( bestFile.exists() ) {
                //hacemos estadisticas del mejor perceptron, si es necesario
                System.out.print("Starting " + experimentName + LearningExperiment.BEST_TRAINED + " Statistics... ");
                processFile(dirPath + experimentName + LearningExperiment.BEST_TRAINED, createNeuralNetworkFile, printHistory);
            }
        }

        //calculamos las estadisticas de los backup si es necesario
        final File[] allFiles = ( new File(dirPath) ).listFiles();
        assert allFiles != null;
        Arrays.sort(allFiles, Comparator.comparingLong(o -> o.lastModified()));
        final List< File >                  backupFiles    = new ArrayList<>();
        final Map< File, StatisticForCalc > resultsPerFile = new HashMap<>();
        for ( final File f : allFiles ) {
            if ( runStatisticsForBackups ) {
                if ( f.getName().matches(".*_BackupN-.*\\.ser") ) {
                    System.out.print("Starting " + f.getName() + " Statistics... ");
                    processFile(dirPath + f.getName().replaceAll("\\.ser$", ""), createNeuralNetworkFile, printHistory);
                    resultsPerFile.put(f, getTileStatistics());
                    backupFiles.add(f);
                }
            } else if ( f.getName().matches(".*_trained\\.ser") ) {
                System.out.print("Starting " + f.getName() + " Statistics... ");
                processFile(dirPath + f.getName().replaceAll("\\.ser$", ""), createNeuralNetworkFile, printHistory);
                resultsPerFile.put(f, getTileStatistics());
                backupFiles.add(f);
            }
        }
        backupFiles.sort(Comparator.comparingLong(o -> o.lastModified()));

        if ( exportToExcel ) {
            exportToExcel(filePath, backupFiles, resultsPerFile, resultsRandom);
        }
    }

    /**
     * @param saveBackupEvery cantidad de partidas en que se debe guardar una copia de respaldo.
     */
    protected
    void saveBackupEvery( final int saveBackupEvery ) {
        this.saveBackupEvery = saveBackupEvery;
    }

    public
    void setBackupStatisticOnly( boolean backupStatisticOnly ) {
        this.backupStatisticOnly = backupStatisticOnly;
    }

    /**
     * @param exportToExcel true si se debe exportar los resultados a una hoja de cálculo.
     */
    public
    void setExportToExcel( final boolean exportToExcel ) {
        this.exportToExcel = exportToExcel;
    }

    /**
     * @param fileName nuevo nombre del archivo sobre el cual trabajar.
     */
    protected
    void setFileName( final String fileName ) {
        this.fileName = fileName;
    }

    /**
     * @param gamesToPlay cantidad de partidas a jugar por hilo, en el cálculo de estadísticas.
     */
    public
    void setGamesToPlayPerThread( final int gamesToPlay ) {
        this.gamesToPlay = gamesToPlay;
    }

    /**
     * @param learningMethod nuevo método TDLearning
     */
    protected
    void setLearningMethod( final TDLambdaLearning learningMethod ) {
        this.learningMethod = learningMethod;
    }

    /**
     * @param runStatisticsForBackups true si debe computar estadísticas sobre los archivos de respaldo.
     */
    public
    void setRunStatisticsForBackups( final boolean runStatisticsForBackups ) {
        this.runStatisticsForBackups = runStatisticsForBackups;
    }

    /**
     * @param threads cantidad de simulaciones a realizar (concurrentemente).
     */
    public
    void setSimulations( final int threads ) {
        simulations = threads;
    }

    /**
     * @param tileToWinForStatistics valor que se considera como ganador, a la hora de ejecutar estadísticas.
     */
    public
    void setTileToWinForStatistics( final int tileToWinForStatistics ) {
        this.tileToWinForStatistics = tileToWinForStatistics;
    }

    /**
     * Inicia el cálculo de estadísticas.
     *
     * @param experimentPath       directorio donde están las redes neuronales sobre las que se calculan las estadísticas.
     * @param createPerceptronFile true si debe crear las redes neuronales faltantes.
     */
    public
    void start(
            final String experimentPath,
            final boolean createPerceptronFile,
            final boolean printHistory
    ) {
        final File experimentPathFile = new File(experimentPath);
        if ( experimentPathFile.exists() && !experimentPathFile.isDirectory() ) {
            throw new IllegalArgumentException("experimentPath must be a directory");
        }
        if ( !experimentPathFile.exists() ) {
            experimentPathFile.mkdirs();
        }
        try {
            learningMethod = null;
            if ( learningExperiment != null ) {
                tileToWin = learningExperiment.getTileToWinForTraining();
                experimentName = learningExperiment.getExperimentName();
            }
            initializeStatistics();
            run(experimentPath, createPerceptronFile, printHistory);
        } catch ( final Exception ex ) {
            Logger.getLogger(StatisticExperiment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
