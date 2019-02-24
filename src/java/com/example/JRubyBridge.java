package com.example;

/**
 * This interface is a bridge between Clojure/Java code and the ruby class
 * `JRubyBridge` (see src/ruby/example-lib/jruby_bridge.rb).
 *
 * The ruby class uses JRuby to "implement" this interface.
 * From Clojure or Java, interact with an instance of the ruby class as if it
 * were an isntance of this interface.
 */
public interface JRubyBridge {
  String rubyVersion();
}
