package ocr.nifi.util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public enum JSONUtils {
  INSTANCE;
  private static ThreadLocal<ObjectMapper> _mapper = new ThreadLocal<ObjectMapper>() {
    /**
     * Returns the current thread's "initial value" for this
     * thread-local variable.  This method will be invoked the first
     * time a thread accesses the variable with the {@link #get}
     * method, unless the thread previously invoked the {@link #set}
     * method, in which case the {@code initialValue} method will not
     * be invoked for the thread.  Normally, this method is invoked at
     * most once per thread, but it may be invoked again in case of
     * subsequent invocations of {@link #remove} followed by {@link #get}.
     * <p>
     * <p>This implementation simply returns {@code null}; if the
     * programmer desires thread-local variables to have an initial
     * value other than {@code null}, {@code ThreadLocal} must be
     * subclassed, and this method overridden.  Typically, an
     * anonymous inner class will be used.
     *
     * @return the initial value for this thread-local
     */
    @Override
    protected ObjectMapper initialValue() {
      return new ObjectMapper();
    }
  };

  public <T> T load(InputStream is, TypeReference<T> ref) throws IOException {
    return _mapper.get().readValue(is, ref);
  }

  public <T> T load(String is, TypeReference<T> ref) throws IOException {
    return _mapper.get().readValue(is, ref);
  }

  public <T> T load(File f, TypeReference<T> ref) throws IOException {
    try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
      return _mapper.get().readValue(is, ref);
    }
  }

  public <T> T load(InputStream is, Class<T> clazz) throws IOException {
    return _mapper.get().readValue(is, clazz);
  }

  public <T> T load(File f, Class<T> clazz) throws IOException {
    try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
      return _mapper.get().readValue(is, clazz);
    }
  }

  public <T> T load(String is, Class<T> clazz) throws IOException {
    return _mapper.get().readValue(is, clazz);
  }

  public String toJSON(Object o, boolean pretty) throws JsonProcessingException {
    if (pretty) {
      return _mapper.get().writerWithDefaultPrettyPrinter().writeValueAsString(o);
    } else {
      return _mapper.get().writeValueAsString(o);
    }
  }

  public byte[] toJSON(Object config) throws JsonProcessingException {
    return _mapper.get().writeValueAsBytes(config);
  }
}
