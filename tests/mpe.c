#include "mmio.h"
#include "kprintf.h"

#define MPE_BASE 0x70000000
#define INC      0x00001000
#define INIT     0x77FFF000

// DOC include start: GCD test
int main(void)
{

  reg_write32(INIT, 0x111);
  reg_write32(INIT, 0x222);
  reg_write32(INIT, 0x333);
  reg_write32(INIT, 0x444);
  reg_write32(INIT, 0x555);
  reg_write32(INIT, 0x666);
  reg_write32(INIT, 0x777);
  reg_write32(INIT, 0x888);

  
  uint64_t in0 = 0x12345678;
  uint64_t in1 = 0x23456789;
  uint64_t in2 = 0x3456789A;
  uint64_t in3 = 0x456789AB;
  uint64_t in4 = 0x56789ABC;


  reg_write32(MPE_BASE+INC*0, 0xABC);
  reg_write32(MPE_BASE+INC*0, 0xBCD);
  reg_write32(MPE_BASE+INC*0, 0xCDE);
  reg_write32(MPE_BASE+INC*0, 0xDEF);
  reg_write32(MPE_BASE+INC*0, 0xAAA);
  reg_write32(MPE_BASE+INC*0, 0xBBB);
  reg_write32(MPE_BASE+INC*0, 0xCCC);
  reg_write32(MPE_BASE+INC*0, in0);

  reg_write32(MPE_BASE+INC*1, 0xCBA);
  reg_write32(MPE_BASE+INC*1, 0xDCB);
  reg_write32(MPE_BASE+INC*1, 0xEDC);
  reg_write32(MPE_BASE+INC*1, 0xFED);
  reg_write32(MPE_BASE+INC*1, 0xFFF);
  reg_write32(MPE_BASE+INC*1, 0xEEE);
  reg_write32(MPE_BASE+INC*1, 0xDDD);
  reg_write32(MPE_BASE+INC*1, in1);

  reg_write32(MPE_BASE+INC*2, 0x222);
  reg_write32(MPE_BASE+INC*2, 0x444);
  reg_write32(MPE_BASE+INC*2, 0x666);
  reg_write32(MPE_BASE+INC*2, 0x888);
  reg_write32(MPE_BASE+INC*2, 0xAAA);
  reg_write32(MPE_BASE+INC*2, 0xCCC);
  reg_write32(MPE_BASE+INC*2, 0xEEE);
  reg_write32(MPE_BASE+INC*2, in2);

  reg_write32(MPE_BASE+INC*3, 0x333);
  reg_write32(MPE_BASE+INC*3, 0x555);
  reg_write32(MPE_BASE+INC*3, 0x777);
  reg_write32(MPE_BASE+INC*3, 0x999);
  reg_write32(MPE_BASE+INC*3, 0xBBB);
  reg_write32(MPE_BASE+INC*3, 0xEEE);
  reg_write32(MPE_BASE+INC*3, 0xDDD);
  reg_write32(MPE_BASE+INC*3, in3);

  reg_write32(MPE_BASE+INC*4, 0x444);
  reg_write32(MPE_BASE+INC*4, 0xDCB);
  reg_write32(MPE_BASE+INC*4, 0xEDC);
  reg_write32(MPE_BASE+INC*4, 0xFED);
  reg_write32(MPE_BASE+INC*4, 0xFFF);
  reg_write32(MPE_BASE+INC*4, 0xEEE);
  reg_write32(MPE_BASE+INC*4, 0xDDD);
  reg_write32(MPE_BASE+INC*4, in4);

  kprintf("Write: %x %x %x %x\n", in1, in2, in3, in4);

  uint32_t result0 = reg_read32(MPE_BASE+INC*0);
  uint32_t result1 = reg_read32(MPE_BASE+INC*1);
  uint32_t result2 = reg_read32(MPE_BASE+INC*2);
  uint32_t result3 = reg_read32(MPE_BASE+INC*3);
  uint32_t result4 = reg_read32(MPE_BASE+INC*4);

  // uint64_t result = result0+result1+result2+result3+result4;
  // printf("Hardware result %d %d\n", result0);
  // printf("Write: %x %x %x %x\n", in1, in2, in3, in4);
  kprintf("Read:  %x %x %x %x\n", result1, result2, result3, result4);
  return 0;
}
// DOC include end: GCD test
