GCC=riscv64-unknown-elf-gcc
# GCC=riscv64-unknown-linux-gnu-gcc
OBJCOPY=riscv64-unknown-elf-objcopy
OBJDUMP=riscv64-unknown-elf-objdump
CFLAGS= -std=gnu99 -O2 -fno-common -fno-builtin-printf -Wall
CFLAGS+= -I include -I.
LDFLAGS= -static

include libgloss.mk

PROGRAMS = test
# test_unknown
# test_unknown 
# fpga 
# mpe pwm blkdev accum charcount nic-loopback big-blkdev pingd \
#            streaming-passthrough streaming-fir nvdla spiflashread spiflashwrite fft gcd

spiflash.img: spiflash.py
	python3 $<

.DEFAULT_GOAL := default

.PHONY: default
default: mpe.bin
# default: $(addsuffix .riscv,$(PROGRAMS)) spiflash.img
# default: $(addsuffix .o,$(PROGRAMS)) spiflash.img

.PHONY: dumps
dumps: $(addsuffix .dump,$(PROGRAMS))

mpe.o: mpe.c
	$(GCC) $(CFLAGS) -c mpe.c -o $@

kprintf.o: kprintf.c
	$(GCC) $(CFLAGS) -c kprintf.c -o $@

# mpe.elf: mpe.o kprintf.o $(libgloss)
# 	$(GCC) $(LDFLAGS) -o $@ mpe.o kprintf.o $(libgloss)
mpe.elf: mpe.o kprintf.o
	$(GCC) $(LDFLAGS) -o $@ mpe.o kprintf.o

mpe.bin: mpe.elf
	$(OBJCOPY) -O binary --change-addresses=-0x80000000 $< $@

%.o: %.S
	$(GCC) $(CFLAGS) -D__ASSEMBLY__=1 -c $< -o $@

%.o: %.c mmio.h spiflash.h
	$(GCC) $(CFLAGS) -c $< -o $@

%.riscv: %.o $(libgloss)
	$(GCC) $(LDFLAGS) $< -o $@

%.dump: %.riscv
	$(OBJDUMP) -D $< > $@


.PHONY: clean
clean:
	rm -f *.riscv *.o *.dump
	rm -f *.elf*.bin
	$(if $(libgloss),rm -rf $(libgloss_builddir)/)
