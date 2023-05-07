# Data word size
word_size = 32
# Number of words in the memory
num_words = 256
num_rw_ports = 0
num_r_ports = 1
num_w_ports = 1
# Technology to use in $OPENRAM_TECH
tech_name = "freepdk45"
RamName="{0}_words".format(num_words)
# You can use the technology nominal corner only
nominal_corner_only = True
# Or you can specify particular corners
# Process corners to characterize
# process_corners = ["SS", "TT", "FF"]
# Voltage corners to characterize
# supply_voltages = [ 3.0, 3.3, 3.5 ]
# Temperature corners to characterize
# temperatures = [ 0, 25 100]

route_supplies = False
check_lvsdrc = False
perimeter_pins = False
# Output directory for the results
output_path = "temp/freepdk45/{0}/DualPort/Ram_{1}_{2}".format(RamName,word_size,num_words)
# Output file base name
output_name = "Ram_{0}".format(RamName)

# Disable analytical models for full characterization (WARNING: slow!)
# analytical_delay = False
