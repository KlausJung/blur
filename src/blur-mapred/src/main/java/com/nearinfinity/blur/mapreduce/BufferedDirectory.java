package com.nearinfinity.blur.mapreduce;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

public class BufferedDirectory extends Directory {

  private Directory _directory;
  private int _buffer;

  public BufferedDirectory(Directory directory, int buffer) {
    _directory = directory;
    _buffer = buffer;
  }

  public void close() throws IOException {
    _directory.close();
  }

  public IndexOutput createOutput(String name) throws IOException {
    return _directory.createOutput(name);
  }

  public void deleteFile(String name) throws IOException {
    _directory.deleteFile(name);
  }

  public boolean fileExists(String name) throws IOException {
    return _directory.fileExists(name);
  }

  public long fileLength(String name) throws IOException {
    return _directory.fileLength(name);
  }

  public long fileModified(String name) throws IOException {
    return _directory.fileModified(name);
  }

  public String[] listAll() throws IOException {
    return _directory.listAll();
  }

  public IndexInput openInput(String name, int bufferSize) throws IOException {
    return openInput(name);
  }

  public IndexInput openInput(String name) throws IOException {
    return new BigBufferIndexInput(name, _directory.openInput(name), _buffer);
  }

  @SuppressWarnings("deprecation")
  public void touchFile(String name) throws IOException {
    _directory.touchFile(name);
  }

  public static class BigBufferIndexInput extends BufferedIndexInput {

    private IndexInput _input;
    private long _length;

    public BigBufferIndexInput(String name, IndexInput input, int buffer) {
      super(name, buffer);
      _input = input;
      _length = input.length();
    }

    @Override
    protected void readInternal(byte[] b, int offset, int length) throws IOException {
      _input.seek(getFilePointer());
      _input.readBytes(b, offset, length);
    }

    @Override
    protected void seekInternal(long pos) throws IOException {

    }

    @Override
    public void close() throws IOException {
      _input.close();
    }

    @Override
    public long length() {
      return _length;
    }

    @Override
    public Object clone() {
      BigBufferIndexInput clone = (BigBufferIndexInput) super.clone();
      clone._input = (IndexInput) _input.clone();
      return clone;
    }
  }

  public void clearLock(String name) throws IOException {
    _directory.clearLock(name);
  }

  public LockFactory getLockFactory() {
    return _directory.getLockFactory();
  }

  public String getLockID() {
    return _directory.getLockID();
  }

  public Lock makeLock(String name) {
    return _directory.makeLock(name);
  }

  public void setLockFactory(LockFactory lockFactory) throws IOException {
    _directory.setLockFactory(lockFactory);
  }

  public void sync(Collection<String> names) throws IOException {
    _directory.sync(names);
  }

  @SuppressWarnings("deprecation")
  public void sync(String name) throws IOException {
    _directory.sync(name);
  }

}
