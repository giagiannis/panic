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

		# Linear function
		# 
		class LinearFunction < Function
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
				return sum
			end
		end


		class ReverseLinearFunction < Function
			def initialize coefficients
				super()
				@coefficients = coefficients
			end

			# returns the evaluation of the expression
			# 1/(c+c1x1+c2x2+...+c3x3)
			def evaluate_expression vector
				sum = 0
				@coefficients.length.times { |index|
					sum+=@coefficients[index]*vector[index]
				}
				return 1.0/sum
			end
		end

		class LogFunction < Function
			def initialize coefficients
				super()
				@coefficients = coefficients
			end


			def evaluate_expression vector
				sum = 0
				@coefficients.length.times{ |index|
					sum+=@coefficients[index]*vector[index]
				}
				return  Math.log(sum)
			end
		end
		class AbsFunction < Function
			def initialize coefficients
				super()
				@coefficients = coefficients
			end
			def evaluate_expression vector
				sum0 = 0.0
				vector.length.times{ |index|
					sum0+=(@coefficients[index]*(vector[index])-0.5)
				}

				return sum0.abs
			end
		end


		class MexicanHatFunction < Function
			def initialize coefficients
				super()
				@coefficients = coefficients
				@sigma = @coefficients[-1]
			end
			def evaluate_expression vector
				sum = 0
				vector.length.times{ |index|
					sum+=(@coefficients[index]*(vector[index]-0.5))**2
				}
				division = sum/(2*@sigma**2)
				return -1/(Math::PI*@sigma**4) * (1-division)*Math.exp(-division)
			end
		end
		class Comp1Function < Function
			def initialize coefficients
				super()
				@coefficients = coefficients
			end
			def evaluate_expression vector
				sum0 = 0
				sum1 = 0
				vector.length.times{ |index|
					sum0+=@coefficients[index]*(vector[index])
					sum1+=vector[index]
				}
				return Math::cos(sum0)*Math::exp(sum1)
			end
		end

		class Comp2Function < Function
			def initialize coefficients
				super()
				@coefficients = coefficients
			end
			def evaluate_expression vector
				sum0 = 0.0
				vector.length.times{ |index|
					sum0+=(@coefficients[index]*(vector[index])-0.5)
				}

				return Math::exp(sum0.abs)-1.0
			end
		end
		class Comp3Function < Function
			def initialize coefficients
				super()
				@coefficients = coefficients
				@offsets = []
				@coefficients.length.times { |index|
					@offsets[index]=rand
				}

			end
			def evaluate_expression vector
				sum0 = 0.0
				vector.length.times{ |index|
					sum0+=(@coefficients[index]*(vector[index])-0.5)
				}
				return Math::exp(sum0.abs)-1.0 + self.gaussian_abnormality(vector)
			end
			def gaussian_abnormality vector
				gauss = 0
				@coefficients.length.times { |index|
					gauss+=(((vector[index]-@offsets[index])**2)/0.05);
				}
				return Math::exp(-gauss)
			end
		end
	end
end

