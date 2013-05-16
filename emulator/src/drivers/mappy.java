/*
This file is part of Arcadeflex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * ported to v0.28
 * ported to v0.27
 *
 *
 *  uses romset from v0.36
 */
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static machine.mappy.*;
import static vidhrdw.mappy.*;
import static sndhrdw.mappy.*;
import static mame.memoryH.*;
public class mappy {
    /* CPU 1 read addresses */
    static MemoryReadAddress mappy_readmem_cpu1[] =
    {
            new MemoryReadAddress( 0x8000, 0xffff, MRA_ROM ),                                 /* ROM code */
            new MemoryReadAddress( 0x4040, 0x43ff, MRA_RAM, mappy_sharedram ),               /* shared RAM with the sound CPU */
            new MemoryReadAddress( 0x4800, 0x480f, mappy_customio_r_1, mappy_customio_1 ),   /* custom I/O chip #1 interface */
            new MemoryReadAddress( 0x4810, 0x481f, mappy_customio_r_2, mappy_customio_2 ),   /* custom I/O chip #2 interface */
            new MemoryReadAddress( 0x0000, 0x9fff, mappy_cpu1ram_r ),                         /* RAM everywhere else */
            new MemoryReadAddress( -1 )	/* end of table */
    };


    static MemoryReadAddress digdug2_readmem_cpu1[] =
    {
            new MemoryReadAddress( 0x8000, 0xffff, MRA_ROM ),                                 /* ROM code */
            new MemoryReadAddress( 0x4040, 0x43ff, MRA_RAM, mappy_sharedram ),               /* shared RAM with the sound CPU */
            new MemoryReadAddress( 0x4800, 0x480f, mappy_customio_r_1, mappy_customio_1 ),   /* custom I/O chip #1 interface */
            new MemoryReadAddress( 0x4810, 0x481f, mappy_customio_r_2, mappy_customio_2 ),   /* custom I/O chip #2 interface */
            new MemoryReadAddress( 0x0000, 0x9fff, digdug2_cpu1ram_r ),                       /* RAM everywhere else */
            new MemoryReadAddress( -1 )	/* end of table */
    };


    /* CPU 1 write addresses */
    static MemoryWriteAddress writemem_cpu1[] =
    {
            new MemoryWriteAddress( 0x1000, 0x177f, MWA_RAM ),                                 /* general RAM, area 1 */
            new MemoryWriteAddress( 0x1800, 0x1f7f, MWA_RAM ),                                 /* general RAM, area 2 */
            new MemoryWriteAddress( 0x2000, 0x277f, MWA_RAM ),                                 /* general RAM, area 3 */
            new MemoryWriteAddress( 0x4040, 0x43ff, MWA_RAM ),                                 /* shared RAM with the sound CPU */
            new MemoryWriteAddress( 0x0000, 0x07ff, mappy_videoram_w, videoram, videoram_size ),/* video RAM */
            new MemoryWriteAddress( 0x0800, 0x0fff, mappy_colorram_w, colorram ),             /* color RAM */
            new MemoryWriteAddress( 0x1780, 0x17ff, MWA_RAM, spriteram, spriteram_size ),    /* sprite RAM, area 1 */
            new MemoryWriteAddress( 0x1f80, 0x1fff, MWA_RAM, spriteram_2 ),                   /* sprite RAM, area 2 */
            new MemoryWriteAddress( 0x2780, 0x27ff, MWA_RAM, spriteram_3 ),                   /* sprite RAM, area 3 */
            new MemoryWriteAddress( 0x3800, 0x3fff, mappy_scroll_w ),                          /* scroll registers */
            new MemoryWriteAddress( 0x4800, 0x480f, mappy_customio_w_1 ),                      /* custom I/O chip #1 interface */
            new MemoryWriteAddress( 0x4810, 0x481f, mappy_customio_w_2 ),                      /* custom I/O chip #2 interface */
            new MemoryWriteAddress( 0x5002, 0x5003, mappy_interrupt_enable_1_w ),              /* interrupt enable */
            new MemoryWriteAddress( 0x500a, 0x500b, mappy_cpu_enable_w ),                      /* sound CPU enable */
            new MemoryWriteAddress( 0x8000, 0x8000, MWA_NOP ),                                 /* watchdog timer */
            new MemoryWriteAddress( 0x8000, 0xffff, MWA_ROM ),                                 /* ROM code */
            new MemoryWriteAddress( -1 )	/* end of table */
    };


    /* CPU 2 read addresses */
    static MemoryReadAddress mappy_readmem_cpu2[] =
    {
            new MemoryReadAddress( 0xe000, 0xffff, MRA_ROM ),                                 /* ROM code */
            new MemoryReadAddress( 0x0040, 0x03ff, mappy_sharedram_r2 ),                      /* shared RAM with the main CPU */
            new MemoryReadAddress( -1 )	/* end of table */
    };


    static MemoryReadAddress digdug2_readmem_cpu2[] =
    {
            new MemoryReadAddress( 0xe000, 0xffff, MRA_ROM ),                                 /* ROM code */
            new MemoryReadAddress( 0x0040, 0x03ff, digdug2_sharedram_r2 ),                    /* shared RAM with the main CPU */
            new MemoryReadAddress( -1 )	/* end of table */
    };


    /* CPU 2 write addresses */
    static MemoryWriteAddress writemem_cpu2[] =
    {
            new MemoryWriteAddress( 0x0040, 0x03ff, mappy_sharedram_w ),                       /* shared RAM with the main CPU */
            new MemoryWriteAddress( 0x0000, 0x003f, mappy_sound_w, mappy_soundregs ),         /* sound control registers */
            new MemoryWriteAddress( 0x2000, 0x2001, mappy_interrupt_enable_2_w ),              /* interrupt enable */
            new MemoryWriteAddress( 0x2006, 0x2007, mappy_sound_enable_w ),                    /* sound enable */
            new MemoryWriteAddress( 0xe000, 0xffff, MWA_ROM ),                                 /* ROM code */
            new MemoryWriteAddress( -1 )	/* end of table */
    };


    /* input from the outside world */
    static InputPort mappy_input_ports[] =
    {
            new InputPort(	/* DSW1 */
                    0x00,
                    new int[]{ 0, 0, 0, 0, 0, 0, OSD_KEY_F1, 0 }
            ),
                new InputPort(	/* DSW2 */
                    0x00,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
            ),
                new InputPort(	/* IN0 */
                    0x00,
                    new int[]{ 0, OSD_KEY_RIGHT, 0, OSD_KEY_LEFT, 0, OSD_KEY_CONTROL, 0, 0 }
            ),
                new InputPort(	/* IN1 */
                    0x00,
                    new int[]{ OSD_KEY_3, 0, 0, 0, OSD_KEY_1, OSD_KEY_2, 0, 0 }
            ),
                new InputPort(	/* DSW3 */
                    0x00,
                   new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
            ),
                new InputPort( -1 )	/* end of table */
    };


    static InputPort digdug2_input_ports[] =
    {
            new InputPort(	/* DSW1 */
                    0x00,
                   new int[] { 0, 0, 0, 0, 0, 0, OSD_KEY_F1, 0 }
            ),
            new InputPort(	/* DSW2 */
                    0x00,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }
            ),
            new InputPort(	/* IN0 */
                    0x00,
                    new int[]{ OSD_KEY_UP, OSD_KEY_RIGHT, OSD_KEY_DOWN, OSD_KEY_LEFT, 0, OSD_KEY_CONTROL, 0, 0 }
            ),
            new InputPort(	/* IN1 */
                    0x00,
                    new int[]{ OSD_KEY_3, 0, 0, 0, OSD_KEY_1, OSD_KEY_2, 0, 0 }
            ),
            new InputPort(	/* IN2/DSW3 */
                    0x00,
                    new int[]{ 0, 0, 0, 0, 0, OSD_KEY_ALT, 0, 0 }
            ),
            new InputPort( -1 )	/* end of table */
    };


    static TrakPort trak_ports[] =
    {
          new TrakPort(-1)
    };


    /* key descriptions and links */
    static KEYSet mappy_keys[] =
    {
            new KEYSet( 2, 3, "MOVE LEFT"  ),
            new KEYSet( 2, 1, "MOVE RIGHT" ),
            new KEYSet( 2, 5, "OPEN DOOR"  ),
            new KEYSet( -1 )
    };


    static KEYSet digdug2_keys[] =
    {
            new KEYSet( 2, 3, "MOVE LEFT"  ),
            new KEYSet( 2, 1, "MOVE RIGHT" ),
            new KEYSet( 2, 0, "MOVE UP"  ),
            new KEYSet( 2, 2, "MOVE DOWN"  ),
            new KEYSet( 2, 5, "PUMP"  ),
            new KEYSet( 4, 5, "DRILL"  ),
            new KEYSet( -1 )
    };


    /* dipswitch menu data */
    static DSW mappy_dsw[] =
    {
            new DSW( 1, 0xc0, "LIVES",      new String[]{ "3", "5", "1", "2" }, 0 ),
            new DSW( 1, 0x38, "BONUS",      new String[]{ "20K 70K", "20K 60K", "20K 80K", "30K 100K", "20K NONE", "20K 70K 70K", "20K 80K 80K", "NONE" }, 0 ),
            new DSW( 0, 0x03, "DIFFICULTY", new String[]{ "EASY", "MEDIUM", "HARD", "HARDEST" }, 0 ),
            new DSW( 0, 0x20, "DEMO SOUND", new String[]{ "ON", "OFF" }, 1 ),
            new DSW( -1 )
    };


    static DSW digdug2_dsw[] =
    {
            new DSW( 0, 0x20, "LIVES",        new String[]{ "3", "5" }, 0 ),
            new DSW( 4, 0x04, "LEVEL SELECT", new String[]{ "OFF", "ON" }, 0 ),
            new DSW( 4, 0x03, "EXTEND",       new String[]{ "A", "C", "B", "D" }, 0 ),
            new DSW( -1 )
    };


    /* layout of the 8x8x2 character data */
    static GfxLayout charlayout = new GfxLayout
    (
            8,8,	         /* 8*8 characters */
            256,	         /* 256 characters */
            2,             /* 2 bits per pixel */
            new int[]{ 0, 4 },      /* the two bitplanes for 4 pixels are packed into one byte */
            new int[]{ 7*8, 6*8, 5*8, 4*8, 3*8, 2*8, 1*8, 0*8 },   /* characters are rotated 90 degrees */
            new int[]{ 8*8+0, 8*8+1, 8*8+2, 8*8+3, 0, 1, 2, 3 },   /* bits are packed in groups of four */
            16*8	       /* every char takes 16 bytes */
    );


    /* layout of the 16x16x4 sprite data */
    static GfxLayout mappy_spritelayout = new GfxLayout
    (
            16,16,	     /* 16*16 sprites */
            128,	        /* 128 sprites */
            4,	           /* 4 bits per pixel */
            new int[]{ 0, 4, 8192*8, 8192*8+4 },	/* the two bitplanes for 4 pixels are packed into one byte */
            new int[]{ 39 * 8, 38 * 8, 37 * 8, 36 * 8, 35 * 8, 34 * 8, 33 * 8, 32 * 8,
                            7 * 8, 6 * 8, 5 * 8, 4 * 8, 3 * 8, 2 * 8, 1 * 8, 0 * 8 },
            new int[]{ 0, 1, 2, 3, 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
                            24*8+0, 24*8+1, 24*8+2, 24*8+3 },
            64*8	/* every sprite takes 64 bytes */
    );

    static GfxLayout digdug2_spritelayout = new GfxLayout
    (
            16,16,	     /* 16*16 sprites */
            256,	        /* 256 sprites */
            4,	           /* 4 bits per pixel */
            new int[]{ 0, 4, 16384*8, 16384*8+4 },	/* the two bitplanes for 4 pixels are packed into one byte */
            new int[]{ 39 * 8, 38 * 8, 37 * 8, 36 * 8, 35 * 8, 34 * 8, 33 * 8, 32 * 8,
                            7 * 8, 6 * 8, 5 * 8, 4 * 8, 3 * 8, 2 * 8, 1 * 8, 0 * 8 },
            new int[]{ 0, 1, 2, 3, 8*8, 8*8+1, 8*8+2, 8*8+3, 16*8+0, 16*8+1, 16*8+2, 16*8+3,
                            24*8+0, 24*8+1, 24*8+2, 24*8+3 },
            64*8	/* every sprite takes 64 bytes */
    );


    /* pointers to the appropriate memory locations and their associated decode structs */
    static GfxDecodeInfo mappy_gfxdecodeinfo[] =
    {
            new GfxDecodeInfo( 1, 0x0000, charlayout,      0, 64 ),
            new GfxDecodeInfo( 1, 0x1000, mappy_spritelayout, 64*4, 16 ),
            new GfxDecodeInfo( -1 ) /* end of array */
    };


    static GfxDecodeInfo digdug2_gfxdecodeinfo[] =
    {
            new GfxDecodeInfo( 1, 0x0000, charlayout,      0, 64 ),
            new GfxDecodeInfo( 1, 0x1000, digdug2_spritelayout, 64*4, 16 ),
            new GfxDecodeInfo( -1 ) /* end of array */
    };


    static char mappy_color_prom[] =
    {
            /* palette */
            0x00,0xd9,0xa4,0x5d,0x36,0x80,0xa7,0x26,0x68,0x06,0x1d,0x66,0xa8,0xde,0xf6,0x00,
            0x00,0x38,0x36,0x06,0x66,0x00,0x5d,0xf0,0xb7,0x26,0x00,0x00,0xe7,0xd9,0xf6,0x00,
       /* characters */
            0x0f,0x01,0x04,0x08,0x0f,0x0d,0x04,0x08,0x0f,0x02,0x04,0x08,0x0f,0x03,0x04,0x08,
            0x0f,0x0d,0x04,0x08,0x0f,0x03,0x04,0x08,0x0f,0x01,0x04,0x08,0x0f,0x0e,0x00,0x00,
            0x00,0x00,0x00,0x00,0x0f,0x08,0x00,0x00,0x0f,0x06,0x04,0x08,0x0f,0x09,0x00,0x00,
            0x0f,0x0c,0x00,0x00,0x0f,0x07,0x00,0x00,0x0f,0x09,0x07,0x00,0x0f,0x02,0x0e,0x0d,
            0x0f,0x03,0x01,0x01,0x0f,0x03,0x0d,0x01,0x0f,0x09,0x02,0x01,0x0f,0x0c,0x03,0x01,
            0x0f,0x0c,0x0d,0x01,0x0f,0x09,0x03,0x01,0x0f,0x07,0x01,0x01,0x0f,0x07,0x0e,0x01,
            0x0f,0x03,0x0e,0x01,0x0f,0x0d,0x0e,0x02,0x0f,0x0d,0x04,0x08,0x0f,0x00,0x09,0x07,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            /* sprites */
            0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,
            0x00,0x08,0x02,0x03,0x07,0x05,0x06,0x07,0x09,0x01,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,
            0x00,0x09,0x02,0x03,0x08,0x05,0x06,0x07,0x01,0x08,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,
            0x00,0x01,0x02,0x03,0x04,0x05,0x0f,0x07,0x08,0x0f,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,
            0x00,0x05,0x02,0x09,0x09,0x05,0x00,0x00,0x02,0x09,0x00,0x00,0x00,0x05,0x00,0x00,
            0x00,0x02,0x00,0x02,0x05,0x00,0x02,0x00,0x02,0x00,0x05,0x00,0x00,0x05,0x00,0x00,
            0x00,0x05,0x02,0x07,0x07,0x05,0x00,0x07,0x02,0x09,0x00,0x02,0x09,0x05,0x00,0x00,
            0x00,0x02,0x00,0x02,0x05,0x00,0x02,0x00,0x02,0x00,0x05,0x05,0x02,0x05,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f,0x0f
    };


    static char digdug2_color_prom[] =
    {
            /* palette */
            0x00,0x07,0x58,0x3f,0xf6,0xaf,0xe7,0xeb,0xd1,0x6e,0x1c,0x22,0x28,0x8c,0x2f,0x00,
            0x00,0xf6,0xeb,0x06,0x3f,0x39,0xa6,0xe7,0x43,0xaf,0x6e,0x1c,0x22,0x28,0xc0,0x00,
       /* characters */
            0x00,0x01,0x00,0x00,0x0e,0x0e,0x0b,0x0e,0x0e,0x0e,0x0a,0x0b,0x0d,0x0c,0x0e,0x0b,
            0x0e,0x0b,0x0e,0x0b,0x0d,0x0c,0x0d,0x0a,0x0d,0x0c,0x0e,0x0b,0x0d,0x0c,0x0b,0x0e,
            0x00,0x09,0x01,0x02,0x0d,0x0c,0x0e,0x0b,0x00,0x0d,0x0a,0x0b,0x00,0x04,0x00,0x00,
            0x0d,0x0c,0x0e,0x0b,0x00,0x03,0x01,0x02,0x00,0x0e,0x0a,0x0b,0x0e,0x01,0x02,0x00,
            0x0d,0x0c,0x00,0x0b,0x0d,0x0c,0x0a,0x0b,0x0d,0x0c,0x00,0x0b,0x0d,0x0c,0x00,0x0b,
            0x0d,0x0c,0x00,0x0b,0x0e,0x0a,0x0e,0x0a,0x0d,0x0c,0x00,0x0b,0x0d,0x0c,0x0b,0x0b,
            0x0d,0x0c,0x00,0x0b,0x0d,0x0c,0x00,0x0b,0x0d,0x0c,0x0e,0x0b,0x0d,0x0c,0x0b,0x0b,
            0x0d,0x0c,0x00,0x0b,0x0b,0x0b,0x0a,0x0a,0x0a,0x0e,0x0e,0x0e,0x0d,0x0b,0x0a,0x0a,
            0x0d,0x0c,0x00,0x0e,0x0d,0x0c,0x00,0x0e,0x0d,0x0c,0x0b,0x0e,0x0d,0x0c,0x00,0x0e,
            0x0d,0x0c,0x00,0x0b,0x0d,0x0c,0x0d,0x0b,0x0d,0x0c,0x0a,0x0e,0x0d,0x0c,0x0b,0x0a,
            0x0d,0x0c,0x0b,0x0e,0x0d,0x0c,0x0e,0x0e,0x0d,0x0b,0x0b,0x0a,0x0d,0x0b,0x0b,0x0a,
            0x0d,0x0c,0x0d,0x0e,0x0d,0x0c,0x0a,0x0d,0x0e,0x0e,0x0b,0x0b,0x0e,0x0e,0x0a,0x0a,
            0x0d,0x08,0x00,0x0a,0x0e,0x0b,0x0b,0x0a,0x0b,0x0b,0x0b,0x0a,0x0a,0x0a,0x0b,0x0b,
            0x0b,0x0b,0x0b,0x0a,0x0e,0x0b,0x0b,0x0a,0x0e,0x0b,0x0e,0x0a,0x0a,0x0b,0x0b,0x0a,
            0x0a,0x0a,0x0b,0x0a,0x0e,0x0b,0x0a,0x0a,0x0d,0x0c,0x0b,0x0a,0x0d,0x0c,0x0a,0x0a,
            0x0d,0x0c,0x0e,0x0a,0x0d,0x0b,0x0e,0x0a,0x0b,0x0a,0x0e,0x0e,0x0e,0x0e,0x0a,0x0e,
            /* sprites */
            0x0f,0x01,0x0f,0x01,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
            0x0f,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
            0x08,0x04,0x04,0x04,0x04,0x04,0x04,0x04,0x03,0x03,0x03,0x03,0x03,0x03,0x03,0x03,
            0x0f,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
            0x0f,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
            0x0f,0x04,0x07,0x04,0x07,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x07,0x0d,0x04,
            0x0f,0x07,0x04,0x07,0x04,0x0a,0x09,0x08,0x07,0x06,0x05,0x04,0x03,0x07,0x0d,0x04,
            0x0f,0x04,0x07,0x04,0x07,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x07,0x0d,0x04,
            0x0f,0x07,0x04,0x07,0x04,0x0a,0x09,0x08,0x07,0x06,0x05,0x04,0x03,0x07,0x0d,0x04,
            0x08,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
            0x08,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
            0x08,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
            0x0f,0x0f,0x08,0x08,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x00,
            0x0f,0x07,0x0f,0x07,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
            0x01,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
            0x07,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff
    };


    /* waveforms for the audio hardware */
    static char mappy_samples[] =
    {
            0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,
            0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,

            0x88,0x88,0x98,0x98,0xa8,0xa8,0xb8,0xb8,0xc8,0xc8,0xd8,0xd8,0xe8,0xe8,0xf8,0xf8,
            0x08,0x08,0x18,0x18,0x28,0x28,0x38,0x38,0x48,0x48,0x58,0x58,0x68,0x68,0x78,0x78,

            0x88,0xa8,0xb8,0xc8,0xe8,0xe8,0xc8,0xd8,0x18,0x28,0x28,0x08,0x28,0xf8,0x28,0x18,
            0xf8,0xf8,0x18,0xe8,0x08,0xe8,0xd8,0xe8,0x28,0x48,0x28,0x28,0x48,0x58,0x68,0x78,

            0x38,0x58,0x68,0x58,0x48,0x28,0x08,0x08,0x08,0x28,0x48,0x58,0x68,0x58,0x38,0x08,
            0xc8,0xa8,0x98,0xa8,0xb8,0xd8,0xf8,0xf8,0xf8,0xd8,0xb8,0xa8,0x98,0xa8,0xc8,0xf8,

            0xf8,0x28,0x48,0x58,0x68,0x58,0x48,0x28,0xf8,0xc8,0xa8,0x98,0x88,0x98,0xa8,0xc8,
            0xf8,0x38,0x58,0x68,0x58,0x38,0xf8,0xb8,0x98,0x88,0x98,0xb8,0xf8,0x68,0xf8,0x88,

            0x28,0x48,0x48,0x28,0xf8,0xf8,0x08,0x38,0x58,0x68,0x58,0x28,0xe8,0xd8,0xd8,0xf8,
            0x18,0x18,0x08,0xc8,0x98,0x88,0x98,0xb8,0xe8,0xf8,0xf8,0xc8,0xa8,0xa8,0xc8,0xf8,

            0xf8,0x68,0x48,0x18,0x48,0x68,0x28,0xf8,0x48,0x78,0x58,0x08,0x28,0x38,0xf8,0xa8,
            0x08,0x58,0x18,0xc8,0xd8,0xf8,0xa8,0x88,0xb8,0x08,0xd8,0x98,0xb8,0xe8,0xb8,0x98,

            0x68,0x68,0xf8,0xf8,0x68,0x68,0xf8,0xf8,0x68,0x68,0xf8,0xf8,0x68,0x68,0xf8,0xf8,
            0x88,0x88,0xf8,0xf8,0x88,0x88,0xf8,0xf8,0x88,0x88,0xf8,0xf8,0x88,0x88,0xf8,0xf8
    };


    static char digdug2_samples[] =
    {
            0xf8,0x18,0x28,0x38,0x48,0x58,0x58,0x68,0x68,0x68,0x58,0x58,0x48,0x38,0x28,0x18,
            0xf8,0xd8,0xc8,0xb8,0xa8,0x98,0x98,0x88,0x88,0x88,0x98,0x98,0xa8,0xb8,0xc8,0xd8,

            0xf8,0x18,0x28,0x38,0xf8,0x58,0x58,0xf8,0x68,0xf8,0x58,0x58,0xf8,0x38,0x28,0x18,
            0xf8,0xd8,0xf8,0xb8,0xf8,0x98,0xf8,0x88,0xf8,0x88,0xf8,0x98,0xf8,0xb8,0xf8,0xd8,

            0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,0x68,
            0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,0x88,

            0x38,0x58,0x68,0x58,0x48,0x28,0x08,0x08,0x08,0x28,0x48,0x58,0x68,0x58,0x38,0x08,
            0xc8,0xa8,0x98,0xa8,0xb8,0xd8,0xf8,0xf8,0xf8,0xd8,0xb8,0xa8,0x98,0xa8,0xc8,0xf8,

            0xf8,0x28,0x48,0x58,0x68,0x58,0x48,0x28,0xf8,0xc8,0xa8,0x98,0x88,0x98,0xa8,0xc8,
            0xf8,0x38,0x58,0x68,0x58,0x38,0xf8,0xb8,0x98,0x88,0x98,0xb8,0xf8,0x68,0xf8,0x88,

            0xf8,0x68,0x48,0x18,0x48,0x68,0x28,0xf8,0x48,0x78,0x58,0x08,0x28,0x38,0xf8,0xa8,
            0x08,0x58,0x18,0xc8,0xd8,0xf8,0xa8,0x88,0xb8,0x08,0xd8,0x98,0xb8,0xe8,0xb8,0x98,

            0xf8,0x28,0x48,0x48,0x68,0x68,0x48,0x48,0x68,0x68,0x48,0x18,0xf8,0xf8,0xd8,0xd8,
            0xf8,0x18,0x18,0xf8,0xf8,0xd8,0xa8,0x88,0x88,0xa8,0xa8,0x88,0x88,0xa8,0xa8,0xb8,

            0x38,0x48,0x58,0x68,0x28,0x38,0x38,0x48,0x28,0x38,0xf8,0x18,0x28,0x38,0x48,0x48,
            0x98,0xa8,0xa8,0xb8,0xc8,0xd8,0xf8,0xb8,0x98,0xa8,0xb8,0xb8,0xc8,0x88,0x98,0xa8
    };


    /* the machine driver: 2 6809s running at 1MHz */
    static MachineDriver mappy_machine_driver = new MachineDriver
    (

            /* basic machine hardware */
            new MachineCPU[]
                {
                    new MachineCPU(
                            CPU_M6809,
                            1100000,			/* 1.1 Mhz */
                            0,
                            mappy_readmem_cpu1,writemem_cpu1,null,null,
                            mappy_interrupt_1,1
                    ),
			new MachineCPU(
                            CPU_M6809,
                            1100000,			/* 1.1 Mhz */
                            2,	/* memory region #2 */
                            mappy_readmem_cpu2,writemem_cpu2,null,null,
                            mappy_interrupt_2,1
                    )
            },
            60,
            mappy_init_machine,
            
            /* video hardware */
            28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
            mappy_gfxdecodeinfo,
            32,64*4+16*16,
            mappy_vh_convert_color_prom,
            VIDEO_TYPE_RASTER,
            null,
            mappy_vh_start,
            mappy_vh_stop,
            mappy_vh_screenrefresh,

            /* sound hardware */
            mappy_samples,
            null,
            null,
            null,
            mappy_sh_update
    );



    /* the machine driver: 2 6809s running at 1MHz */
    static MachineDriver digdug2_machine_driver = new MachineDriver
    (
            /* basic machine hardware */
            new MachineCPU[] {
			new MachineCPU(
                            CPU_M6809,
                            1600000,			/* 1.6 Mhz */
                            0,
                            digdug2_readmem_cpu1,writemem_cpu1,null,null,
                            mappy_interrupt_1,1
                    ),
			new MachineCPU(
                            CPU_M6809,
                            1600000,			/* 1.6 Mhz */
                            2,	/* memory region #2 */
                            digdug2_readmem_cpu2,writemem_cpu2,null,null,
                            mappy_interrupt_2,1
                    )
            },
            60,
            mappy_init_machine,

            /* video hardware */
            28*8, 36*8, new rectangle( 0*8, 28*8-1, 0*8, 36*8-1 ),
            digdug2_gfxdecodeinfo,
            32,64*4+16*16,
            mappy_vh_convert_color_prom,
            VIDEO_TYPE_RASTER,
            null,
            mappy_vh_start,
            mappy_vh_stop,
            mappy_vh_screenrefresh,

            /* sound hardware */
            digdug2_samples,
            null,
            null,
            null,
            mappy_sh_update
    );



    /* ROM loader description */
    static RomLoadPtr mappy_rom = new RomLoadPtr(){ public void handler()
    {
            ROM_REGION(0x10000);	/* 64k for code for the first CPU  */
            ROM_LOAD( "mappy1d.64", 0xa000, 0x2000, 0x37abe067 );
            ROM_LOAD( "mappy1c.64", 0xc000, 0x2000, 0x860ad848 );
            ROM_LOAD( "mappy1b.64", 0xe000, 0x2000, 0xf2985b78 );

            ROM_REGION(0x5000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "mappy3b.32", 0x0000, 0x1000, 0x28f0a190 );
            ROM_LOAD( "mappy3m.64", 0x1000, 0x2000, 0xd22dfbf1 );
            ROM_LOAD( "mappy3n.64", 0x3000, 0x2000, 0x7289055d );

            ROM_REGION(0x10000);	/* 64k for the second CPU */
            ROM_LOAD( "mappy1k.64", 0xe000, 0x2000, 0x8ad60a6c );
            ROM_END();

      }};



    /* ROM loader description */
      static RomLoadPtr digdug2_rom = new RomLoadPtr(){ public void handler()
      {
            ROM_REGION(0x10000);	/* 64k for code for the first CPU  */
            ROM_LOAD( "ddug2-3.bin", 0x8000, 0x4000, 0x394e5740 );
            ROM_LOAD( "ddug2-1.bin", 0xc000, 0x4000, 0x9609c483 );

            ROM_REGION(0x9000);	/* temporary space for graphics (disposed after conversion) */
            ROM_LOAD( "ddug2-3b.bin", 0x0000, 0x1000, 0x4cc3b18b );
            ROM_LOAD( "ddug2-3m.bin", 0x1000, 0x4000, 0xb64ae942 );
            ROM_LOAD( "ddug2-3n.bin", 0x5000, 0x4000, 0xf6fa8bf6 );

            ROM_REGION(0x10000);	/* 64k for the second CPU */
            ROM_LOAD( "ddug2-4.bin", 0xe000, 0x2000, 0x248ef4ae );
            ROM_END();
       }};



    /* load the high score table */
    static HiscoreLoadPtr mappy_hiload = new HiscoreLoadPtr() { public int handler()
    {
       int writing = 0;
       FILE f;

       /* get RAM pointer (this game is multiCPU, we can't assume the global */
       /* RAM pointer is pointing to the right place) */
       char []RAM = Machine.memory_region[0];

       /* check if the hi score table has already been initialized */
   /*TOFIX    if (memcmp(RAM,0x1465,new char[] {'B','E','H'},3) == 0 &&          /* check for high score initials */
/*TOFIX            memcmp(RAM,0x1385,new char[]{0x00,0x20,0x00 },3) == 0 &&    /* check for main high score value */
/*TOFIX           memcmp(RAM,0x7ed,new char[]{0x00,0x00,0x00},3) == 0)         /* see if main high score was written to screen */
 /*TOFIX      {
          if ((f = fopen(name,"rb")) != null)
          {
             fread(RAM,0x1460,1,40,f);
             fclose(f);

             /* also copy over the high score */
/*TOFIX             RAM[0x1385] = RAM[0x1460];
             RAM[0x1386] = RAM[0x1461];
             RAM[0x1387] = RAM[0x1462];
          }

          /* this is a little gross, but necessary to get the high score on-screen */
/*TOFIX          if (writing==0) writing = (RAM[0x1385] >> 4);
          mappy_videoram_w.handler(0x7f3, writing!=0 ? (RAM[0x1385] >> 4) : ' ');
          if (writing==0) writing = (RAM[0x1385] & 0x0f);
          mappy_videoram_w.handler (0x7f2, writing!=0 ? (RAM[0x1385] & 0x0f) : ' ');
          if (writing==0) writing = (RAM[0x1386] >> 4);
          mappy_videoram_w.handler (0x7f1, writing!=0 ? (RAM[0x1386] >> 4) : ' ');
          if (writing==0) writing = (RAM[0x1386] & 0x0f);
          mappy_videoram_w.handler (0x7f0, writing!=0 ? (RAM[0x1386] & 0x0f) : ' ');
          if (writing==0) writing = (RAM[0x1387] >> 4);
          mappy_videoram_w.handler (0x7ef, writing!=0 ? (RAM[0x1387] >> 4) : ' ');
          if (writing==0) writing = (RAM[0x1387] & 0x0f);
          mappy_videoram_w.handler(0x7ee, writing!=0 ? (RAM[0x1387] & 0x0f) : ' ');
          mappy_videoram_w.handler (0x7ed, 0);

          return 1;
       }
       else*/ return 0; /* we can't load the hi scores yet */
    }};


    /* save the high score table */
    static HiscoreSavePtr mappy_hisave = new HiscoreSavePtr() { public void handler()
    {
       FILE f;

       /* get RAM pointer (this game is multiCPU, we can't assume the global */
       /* RAM pointer is pointing to the right place) */
       char []RAM = Machine.memory_region[0];

  /*TOFIX     if ((f = fopen(name,"wb")) != null)
       {
          fwrite(RAM,0x1460,1,40,f);
          fclose(f);
       }*/
    }};


    /* load the high score table */
    static HiscoreLoadPtr digdug2_hiload = new HiscoreLoadPtr() { public int handler()
    {
       int writing = 0;
       FILE f;

       /* get RAM pointer (this game is multiCPU, we can't assume the global */
       /* RAM pointer is pointing to the right place) */
       char []RAM = Machine.memory_region[0];

       /* check if the hi score table has already been initialized */
  /*TOFIX     if (memcmp(RAM,0x11b6,new char[] {0x00,'K','A','Z','U',0x00},6) == 0 &&         /* check for high score initials */
  /*TOFIX           memcmp(RAM,0x100b,new char[] {0x00,0x20,0x00},3) == 0 &&   /* check for main high score value */
  /*TOFIX           memcmp(RAM,0x7ed,new char[] {0x30,0x00,0x00},3) == 0)          /* see if main high score was written to screen */
 /*TOFIX      {
          if ((f = fopen(name,"rb")) != null)
          {
             fread(RAM,0x11b0,1,80,f);
             fclose(f);

             /* also copy over the high score */
/*TOFIX             RAM[0x100b] = (char)((RAM[0x11b0] << 4) | RAM[0x11b1]);
             RAM[0x100c] = (char)((RAM[0x11b2] << 4) | RAM[0x11b3]);
             RAM[0x100d] = (char)((RAM[0x11b4] << 4) | RAM[0x11b5]);
          }

          /* this is a little gross, but necessary to get the high score on-screen */
 /*TOFIX         if (writing==0) writing = (RAM[0x11b0] & 0x0f);
          mappy_videoram_w.handler(0x7f3, writing!=0 ? (RAM[0x11b0] & 0x0f) : ' ');
          if (writing==0) writing = (RAM[0x11b1] & 0x0f);
          mappy_videoram_w.handler (0x7f2, writing!=0 ? (RAM[0x11b1] & 0x0f) : ' ');
          if (writing==0) writing = (RAM[0x11b2] & 0x0f);
          mappy_videoram_w.handler (0x7f1, writing!=0 ? (RAM[0x11b2] & 0x0f) : ' ');
          if (writing==0) writing = (RAM[0x11b3] & 0x0f);
          mappy_videoram_w.handler (0x7f0, writing!=0 ? (RAM[0x11b3] & 0x0f) : ' ');
          if (writing==0) writing = (RAM[0x11b4] & 0x0f);
          mappy_videoram_w.handler (0x7ef, writing!=0 ? (RAM[0x11b4] & 0x0f) : ' ');
          if (writing==0) writing = (RAM[0x11b5] & 0x0f);
          mappy_videoram_w.handler (0x7ee, writing!=0 ? (RAM[0x11b5] & 0x0f) : ' ');
          mappy_videoram_w.handler (0x7ed, 0x30);

          return 1;
       }
       else */return 0; /* we can't load the hi scores yet */
    }};


    /* save the high score table */
    static HiscoreSavePtr digdug2_hisave = new HiscoreSavePtr() { public void handler()
    {
       FILE f;

       /* get RAM pointer (this game is multiCPU, we can't assume the global */
       /* RAM pointer is pointing to the right place) */
        char []RAM = Machine.memory_region[0];

   /*TOFIX    if ((f = fopen(name,"wb")) != null)
       {
          fwrite(RAM,0x11b0,1,80,f);
          fclose(f);
       }*/
    }};


    /* the core game driver */
    public static GameDriver mappy_driver = new GameDriver
    (
            "Mappy",
            "mappy",
            "AARON GILES\nMIRKO BUFFONI",
            mappy_machine_driver,

            mappy_rom,
            null, null,
            null,

            mappy_input_ports,null, trak_ports, mappy_dsw, mappy_keys,

            mappy_color_prom,null,null,

            ORIENTATION_DEFAULT,

            mappy_hiload, mappy_hisave
    );


    /* the core game driver */
    public static GameDriver digdug2_driver = new GameDriver
    (
            "Dig Dug 2",
            "digdug2",
            "AARON GILES\nMIRKO BUFFONI\nJROK",
            digdug2_machine_driver,

            digdug2_rom,
            null, null,
            null,

            digdug2_input_ports,null, trak_ports, digdug2_dsw, digdug2_keys,

            digdug2_color_prom,null,null,

            ORIENTATION_DEFAULT,

            digdug2_hiload, digdug2_hisave
    );

}
