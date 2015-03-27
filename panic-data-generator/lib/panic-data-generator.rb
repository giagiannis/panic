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


require_relative 'panic-data-generator/functions'
require 'optparse'
include Panic::Functions


options = {}
OptionParser.new do |opts|
  opts.on(
    '-nAMPLITUDE', 
    '--noise AMPLITUDE', 
    'Define the amplitude of the noise. ') { |o| options[:noise] = o }
  opts.on(
    '-cCOEFFICIENTS', 
    '--coefficients COEFFICIENTS', 
    'Coefficients indicating the velocity of decrease per dimension (comma separated doubles)') { |o| options[:coefficients] = o }
  opts.on(
    '-dCARDINALITIES',
    '--cardinalities CARDINALITIES',
    'Cardinality of each dimension (comma separated integers)') { |o| options[:cardinality] = o }
  opts.on(
    '-tTYPE',
    '--type TYPE',
    'Type of function (default is explinear)') {|o| options[:type] = o}
end.parse!
#puts options


conv = options[:coefficients].split(",").map { |x| x=x.to_f  }
a = ExpLinearFunction.new conv
a.noise_amplitude=options[:noise].to_f


i=0.0
while i<=1.0
  puts a.get_value([i, i/2.0])
  i+=0.1
end