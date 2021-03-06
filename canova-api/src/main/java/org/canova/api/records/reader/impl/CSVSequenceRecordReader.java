package org.canova.api.records.reader.impl;

import org.apache.commons.io.IOUtils;
import org.canova.api.io.data.Text;
import org.canova.api.records.reader.SequenceRecordReader;
import org.canova.api.writable.Writable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * CSV Sequence Record Reader
 * This reader is indended to read sequences of data in CSV format, where
 * each sequence is defined in its own file (and there are multiple files)
 * Each line in the file represents one time step
 */
public class CSVSequenceRecordReader extends FileRecordReader implements SequenceRecordReader {
    private int skipNumLines = 0;
    private String delimiter = ",";

    public CSVSequenceRecordReader() {
        this(0, ",");
    }

    public CSVSequenceRecordReader(int skipNumLines) {
        this(skipNumLines, ",");
    }

    public CSVSequenceRecordReader(int skipNumLines, String delimiter) {
        this.skipNumLines = skipNumLines;
        this.delimiter = delimiter;
    }


    @Override
    @SuppressWarnings("unchecked")
    public Collection<Collection<Writable>> sequenceRecord() {
        File next = iter.next();

        Iterator<String> lineIter;
        try {
            lineIter = IOUtils.lineIterator(new InputStreamReader(new FileInputStream(next)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (skipNumLines > 0) {
            int count = 0;
            while (count++ < skipNumLines && lineIter.hasNext()) lineIter.next();
        }

        Collection<Collection<Writable>> out = new ArrayList<>();
        while (lineIter.hasNext()) {
            String line = lineIter.next();
            String[] split = line.split(delimiter);
            ArrayList<Writable> list = new ArrayList<>();
            for (String s : split) list.add(new Text(s));
            out.add(list);
        }

        return out;
    }
}
