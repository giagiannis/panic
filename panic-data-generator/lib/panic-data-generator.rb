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
if options[:type]=="explinear"
  a = ExpLinearFunction.new conv
elsif options[:type]=="gauss"
  a = GaussFunction.new conv
end

a.noise_amplitude=options[:noise].to_f
for j in (0..(options[:cardinality].split(",").size-1))
new_res = Array.new
old_res = Array.new unless old_res
pivot = 1.0/options[:cardinality].split(',')[j].to_i
i=pivot/2.0
while i<=1.0
  if(j>0)
  old_res.each { |tt| 
    temp = tt.dup
    temp << i.round(5)
    new_res << temp
  }
  else
      new_res << [i.round(5)]
  end
  i+=pivot
end
old_res = new_res.dup
end

old_res[0].size().times{ |i|
  print "x"+(i+1).to_s+"\t"
}
puts "y"

for i in old_res
  i << a.get_value(i)
  for f in i
    print "#{f}\t"
  end
  puts
end