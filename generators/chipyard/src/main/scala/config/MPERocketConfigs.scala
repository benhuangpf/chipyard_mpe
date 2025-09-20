package chipyard

import freechips.rocketchip.config.{Config}
import freechips.rocketchip.diplomacy.{AsynchronousCrossing}

// RocketConfigs.scala
class MPERocketConfig extends Config(
  // new chipyard.harness.WithMPEMem ++
  new openmpe.WithMPE ++  
  // new chipyard.config.WithMemoryBusFrequency(50) ++
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
  