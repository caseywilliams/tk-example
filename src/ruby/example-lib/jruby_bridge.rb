require 'java'

# This class is a bridge between ruby code and the java interface
# `com.example.JRubyBridge`.

module Example
  class JRubyBridge
    include Java::com.example.JRubyBridge

    def initialize
      puts "==> JRubyBridge#initialize called in Ruby"
    end

    # This rubyVersion method is part of the JRubyBridge java interface
    def rubyVersion
      # Just return the version of ruby
      RUBY_VERSION
    end
  end
end
