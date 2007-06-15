package edu.cmu.sphinx.frontend.test;

import edu.cmu.sphinx.frontend.*;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A DataProcessor implemenation which can be used to setup simple unit-tests for other DataProcessors. Addtionally some
 * static utility methods which should ease unit-testing of DataProcessors are provided by this class.
 *
 * @author Holger Brandl
 */
public abstract class AbstractTestProcessor extends BaseDataProcessor {

    public static Random r = new Random(123);

    protected List<Data> input;


    @Before
    public void setUp() {
        input = new ArrayList<Data>();
    }


    public Data getData() throws DataProcessingException {
        return input.remove(0);
    }


    public List<Data> collectOutput(BaseDataProcessor dataProc) throws DataProcessingException {
        dataProc.setPredecessor(this);

        List<Data> output = new ArrayList<Data>();

        while (true) {
            Data d = dataProc.getData();
            output.add(d);

            if (d instanceof DataEndSignal)
                return output;
        }
    }


    public static List<DoubleData> createFeatVectors(double lengthSec, int sampleRate, long startSample, int featDim, double shiftMs) {
        int numFrames = (int) Math.ceil((lengthSec * 1000) / shiftMs);
        List<DoubleData> datas = new ArrayList<DoubleData>(numFrames);

        long curStartSample = startSample;
        long shiftSamples = ms2samples((int) shiftMs, sampleRate);
        for (int i = 0; i < numFrames; i++) {
            double[] values = createRandFeatureVector(featDim);
            datas.add(new DoubleData(values, sampleRate, 0, curStartSample));

            curStartSample += shiftSamples;
        }

        return datas;
    }


    public static double[] createRandFeatureVector(int featDim) {
        double[] updBlock = new double[featDim];

        for (int i = 0; i < updBlock.length; i++) {
            updBlock[i] = 10 * r.nextDouble(); // *10 to get better debuggable (sprich: merkbarer) values
        }

        return updBlock;
    }


    public static long ms2samples(double ms, int sampleRate) {
        return Math.round(sampleRate * ms / 1000);
    }
}