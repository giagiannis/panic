#   Copyright 2015 Giannis Giannakopoulos
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

#
module Panic
  #
  module Functions
    # Base function used to hold the interface of any function and implement
    # the base logic of each other function. 
    class Function
      attr_writer :noise_amplitude
      # Default constructor, allocating all the necessary structures
      def initialize
        @noise_amplitude = 0.0
      end
      
      # Returns the value for a specific vector. Both the input and the output
      # values are normalized (in the range [0.0,1.0]).
      def get_value vector
        return self.evaluate_expression(vector) * (1+(rand()-0.5)*@noise_amplitude)
      end
      
      # This method is override by any implementing subclass and it contains the actual
      # calculation of the expression to be returned
      def evaluate_expression vector
        raise NotImplementedError
      end
    end
    
    # Function that expresses the performance function as an exponential relationship
    # of a linear combination of the independent variables.
    class ExpLinearFunction < Function
      def initialize coefficients
        super()
        @coefficients = coefficients
      end
      
      # returns the evaluation of the expression
      # c+c1x1+c2x2+...+c3x3
      def evaluate_expression vector
        sum = 0
        @coefficients.length.times { |index|
          sum+=@coefficients[index]*vector[index]
        }
        return 1-Math.exp(-sum**2)
      end
    end  
    # Function that expresses the performance function as a "bell", in which 
    # 
    class GaussFunction < Function
      def initialize coefficients
        super()
        @coefficients = coefficients
      end
      
      # returns the evaluation of the expression
      # c+c1x1+c2x2+...+c3x3
      def evaluate_expression vector
        sum = 0
        @coefficients.length.times { |index|
          sum+=@coefficients[index]*(((vector[index]-0.5)**2)/0.5);
        }
        return Math.exp(-sum)
      end
    end
  end
end

