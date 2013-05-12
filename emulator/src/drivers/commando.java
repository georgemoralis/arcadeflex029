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
/*
 * ported to v0.28
 * ported to v0.27
 *
 *  Notes : Roms are from v0.36 romset
 *
 */

package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.inptport.*;
import static mame.osdependH.*;
import static vidhrdw.generic.*;
import static sndhrdw.generic.*;
import static sndhrdw._8910intf.*;
import static sndhrdw.capcom.*;
import static vidhrdw.commando.*;

public class commando {
  static MemoryReadAddress readmem[] =
    {
            new MemoryReadAddress( 0xe000, 0xfdff, MRA_RAM ),
            new MemoryReadAddress( 0x0000, 0xbfff, MRA_ROM ),
            new MemoryReadAddress( 0xd000, 0xd7ff, MRA_RAM ),
            new MemoryReadAddress( 0xc000, 0xc000, input_port_0_r ),
            new MemoryReadAddress( 0xc001, 0xc001, input_port_1_r ),
            new MemoryReadAddress( 0xc002, 0xc002, input_port_2_r ),
            new MemoryReadAddress( 0xc003, 0xc003, input_port_3_r ),
            new MemoryReadAddress( 0xc004, 0xc004, input_port_4_r ),
            new MemoryReadAddress( -1 )	/* end of table */
    };

    static MemoryWriteAddress writemem[] =
    {
            new MemoryWriteAddress( 0xe000, 0xfdff, MWA_RAM ),
            new MemoryWriteAddress( 0xd000, 0xd3ff, videoram_w, videoram, videoram_size ),
            new MemoryWriteAddress( 0xd400, 0xd7ff, colorram_w, colorram ),
            new MemoryWriteAddress( 0xd800, 0xdbff, commando_bgvideoram_w, commando_bgvideoram, commando_bgvideoram_size ),
            new MemoryWriteAddress( 0xdc00, 0xdfff, commando_bgcolorram_w, commando_bgcolorram ),
            new MemoryWriteAddress( 0xfe00, 0xff7f, MWA_RAM, spriteram, spriteram_size ),
            new MemoryWriteAddress( 0xc808, 0xc809, MWA_RAM, commando_scrolly ),
            new MemoryWriteAddress( 0xc80a, 0xc80b, MWA_RAM, commando_scrollx ),
            new MemoryWriteAddress( 0xc800, 0xc800, sound_command_w ),
            new MemoryWriteAddress( 0x0000, 0xbfff, MWA_ROM ),
            new MemoryWriteAddress( -1 )	/* end of table */
    };



    static MemoryReadAddress sound_readmem[] =
    {
            new MemoryReadAddress( 0x4000, 0x47ff, MRA_RAM ),
            new MemoryReadAddress( 0x6000, 0x6000, sound_command_latch_r ),
            new MemoryReadAddress( 0x0000, 0x3fff, MRA_ROM ),
            new MemoryReadAddress( -1 )	/* end of table */
    };

    static MemoryWriteAddress sound_writemem[] =
    {
            new MemoryWriteAddress( 0x4000, 0x47ff, MWA_RAM ),
            new MemoryWriteAddress( 0x8000, 0x8000, AY8910_control_port_0_w ),
            new MemoryWriteAddress( 0x8001, 0x8001, AY8910_write_port_0_w ),
            new MemoryWriteAddress( 0x8002, 0x8002, AY8910_control_port_1_w ),
            new MemoryWriteAddress( 0x8003, 0x8003, AY8910_write_port_1_w ),
            new MemoryWriteAddress( 0x0000, 0x3fff, MWA_ROM ),
            new MemoryWriteAddress( -1 )	/* end of table */
    };



    static InputPort input_ports[] =
    {
            new InputPort(	/* IN0 */
                    0xff,
                    new int[]{ OSD_KEY_1, OSD_KEY_2, 0, 0, 0, 0, 0, OSD_KEY_3 }

            ),
            new InputPort(	/* IN1 */
                    0xff,
                    new int[]{ OSD_KEY_RIGHT, OSD_KEY_LEFT, OSD_KEY_DOWN, OSD_KEY_UP,
                                    OSD_KEY_CONTROL, OSD_KEY_ALT, 0, 0 }

            ),
            new InputPort(	/* IN2 */
                    0xff,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }

            ),
            new InputPort(	/* DSW1 */
                    0xff,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }

            ),
            new InputPort(	/* DSW2 */
                    0x3f,
                    new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 }

            ),
            new InputPort( -1 )	/* end of table */
    };

    static TrakPort trak_ports[] =
    {
            new TrakPort(-1)
    };


    static KEYSet keys[] =
    {
             new KEYSet( 1, 3, "MOVE UP" ),
             new KEYSet( 1, 1, "MOVE LEFT"  ),
             new KEYSet( 1, 0, "MOVE RIGHT" ),
             new KEYSet( 1, 2, "MOVE DOWN" ),
             new KEYSet( 1, 4, "FIRE" ),
             new KEYSet( 1, 5, "GRENADE" ),
             new KEYSet( -1 )
    };


    static DSW dsw[] =
    {
            new DSW( 3, 0x0c, "LIVES",new String[] { "5", "2", "4", "3" }, 1 ),
            new DSW( 4, 0x07, "BONUS",new String[] { "NONE", "20000 700000", "30000 800000", "10000 600000", "40000 1000000", "20000 600000", "30000 700000", "10000 500000" }, 1 ),
            new DSW( 4, 0x10, "DIFFICULTY",new String[] { "DIFFICULT", "NORMAL" }, 1 ),
            new DSW( 3, 0x03, "STARTING STAGE",new String[] { "7", "3", "5", "1" }, 1 ),
            new DSW( 4, 0x08, "DEMO SOUNDS",new String[] { "OFF", "ON" } ),
            new DSW( -1 )
    };



    static GfxLayout charlayout = new GfxLayout
    (
            8,8,	/* 8*8 characters */
            1024,	/* 1024 characters */
            2,	/* 2 bits per pixel */
            new int[]{ 4, 0 },
            new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
            new int[]{ 8+3, 8+2, 8+1, 8+0, 3, 2, 1, 0 },
            16*8	/* every char takes 16 consecutive bytes */
    );
    static GfxLayout tilelayout = new GfxLayout
    (
            16,16,	/* 16*16 tiles */
            1024,	/* 1024 tiles */
            3,	/* 3 bits per pixel */
            new int[]{ 0, 1024*32*8, 2*1024*32*8 },	/* the bitplanes are separated */
            new int[]{ 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
                            8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
            new int[]{ 16*8+7, 16*8+6, 16*8+5, 16*8+4, 16*8+3, 16*8+2, 16*8+1, 16*8+0,
                            7, 6, 5, 4, 3, 2, 1, 0 },
            32*8	/* every tile takes 32 consecutive bytes */
    );
    static GfxLayout spritelayout = new GfxLayout
    (
            16,16,	/* 16*16 sprites */
            768,	/* 768 sprites */
            4,	/* 4 bits per pixel */
            new int[]{ 768*64*8+4, 768*64*8+0, 4, 0 },
            new int[]{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
                            8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
            new int[]{ 33*8+3, 33*8+2, 33*8+1, 33*8+0, 32*8+3, 32*8+2, 32*8+1, 32*8+0,
                            8+3, 8+2, 8+1, 8+0, 3, 2, 1, 0 },
            64*8	/* every sprite takes 64 consecutive bytes */
    );



    static GfxDecodeInfo gfxdecodeinfo[] =
    {
            new GfxDecodeInfo( 1, 0x00000, charlayout,           0, 16 ),
            new GfxDecodeInfo( 1, 0x04000, tilelayout,   16*4+4*16, 16 ),
            new GfxDecodeInfo( 1, 0x1c000, spritelayout,      16*4, 4 ),
            new GfxDecodeInfo( -1 ) /* end of array */
    };



    /* these are NOT the original color PROMs */
    static char color_prom[] =
    {
	/* 1D: palette red component */
	0x00,0x08,0x00,0x09,0x06,0x00,0x07,0x03,0x04,0x08,0x0A,0x07,0x05,0x03,0x07,0x05,
	0x03,0x08,0x00,0x06,0x04,0x00,0x07,0x03,0x0A,0x08,0x09,0x06,0x04,0x03,0x06,0x04,
	0x0A,0x08,0x05,0x05,0x04,0x09,0x05,0x07,0x00,0x00,0x00,0x00,0x06,0x04,0x07,0x0B,
	0x00,0x09,0x0A,0x08,0x0B,0x07,0x04,0x06,0x03,0x08,0x07,0x0A,0x08,0x06,0x04,0x08,
	0x05,0x08,0x00,0x09,0x06,0x00,0x07,0x03,0x08,0x03,0x0A,0x09,0x07,0x07,0x05,0x04,
	0x09,0x08,0x0A,0x07,0x06,0x05,0x04,0x07,0x00,0x08,0x06,0x05,0x04,0x09,0x07,0x0B,
	0x00,0x08,0x06,0x05,0x04,0x09,0x07,0x08,0x00,0x08,0x08,0x0A,0x08,0x06,0x00,0x0C,
	0x00,0x09,0x07,0x06,0x04,0x0A,0x06,0x04,0x00,0x08,0x0A,0x07,0x06,0x05,0x04,0x07,
	0x01,0x00,0x00,0x00,0x0C,0x0A,0x08,0x0C,0x07,0x0C,0x0A,0x0A,0x08,0x06,0x04,0x00,
	0x01,0x0A,0x08,0x06,0x06,0x04,0x02,0x0C,0x07,0x06,0x0B,0x09,0x07,0x05,0x04,0x00,
	0x01,0x0A,0x07,0x05,0x09,0x09,0x05,0x0B,0x05,0x03,0x07,0x08,0x07,0x06,0x04,0x00,
	0x01,0x07,0x05,0x03,0x08,0x04,0x06,0x0C,0x05,0x0F,0x09,0x0A,0x09,0x07,0x05,0x00,
	0x0A,0x0A,0x00,0x00,0x0C,0x0B,0x00,0x00,0x0C,0x0B,0x00,0x00,0x0C,0x00,0x00,0x00,
	0x0C,0x00,0x00,0x00,0x0C,0x00,0x00,0x00,0x0C,0x0A,0x00,0x00,0x0A,0x00,0x04,0x00,
	0x0B,0x03,0x06,0x00,0x05,0x0B,0x0A,0x00,0x05,0x0B,0x00,0x00,0x0B,0x05,0x08,0x00,
	0x03,0x07,0x05,0x00,0x04,0x0A,0x00,0x00,0x0C,0x00,0x03,0x00,0x08,0x04,0x06,0x00,
	/* 2D: palette green component */
	0x00,0x05,0x05,0x06,0x03,0x07,0x04,0x04,0x03,0x06,0x08,0x08,0x06,0x04,0x05,0x04,
	0x04,0x06,0x05,0x08,0x06,0x07,0x05,0x04,0x08,0x06,0x0B,0x08,0x06,0x04,0x04,0x03,
	0x08,0x06,0x05,0x06,0x04,0x07,0x04,0x05,0x00,0x09,0x06,0x04,0x06,0x04,0x05,0x09,
	0x00,0x07,0x08,0x06,0x09,0x05,0x03,0x04,0x03,0x06,0x07,0x0A,0x08,0x06,0x04,0x05,
	0x04,0x06,0x05,0x07,0x04,0x07,0x05,0x04,0x05,0x04,0x09,0x08,0x06,0x04,0x04,0x03,
	0x07,0x06,0x0A,0x07,0x06,0x05,0x04,0x05,0x00,0x05,0x03,0x02,0x00,0x06,0x04,0x09,
	0x00,0x05,0x03,0x02,0x00,0x06,0x04,0x05,0x00,0x06,0x05,0x09,0x07,0x05,0x00,0x0A,
	0x00,0x07,0x05,0x04,0x03,0x08,0x06,0x04,0x00,0x05,0x0A,0x07,0x06,0x05,0x04,0x04,
	0x01,0x09,0x06,0x04,0x0A,0x08,0x06,0x0A,0x05,0x0C,0x00,0x0A,0x08,0x06,0x04,0x00,
	0x01,0x09,0x07,0x05,0x08,0x06,0x04,0x0A,0x05,0x06,0x0A,0x09,0x07,0x05,0x04,0x00,
	0x01,0x0A,0x07,0x05,0x08,0x06,0x02,0x09,0x05,0x04,0x06,0x05,0x04,0x03,0x00,0x00,
	0x01,0x09,0x06,0x04,0x06,0x03,0x04,0x0A,0x05,0x0E,0x0C,0x08,0x07,0x05,0x04,0x00,
	0x00,0x0A,0x00,0x00,0x0C,0x0A,0x00,0x00,0x0C,0x06,0x00,0x00,0x0C,0x00,0x00,0x00,
	0x0C,0x00,0x00,0x00,0x0C,0x08,0x00,0x00,0x0C,0x00,0x00,0x00,0x00,0x06,0x04,0x00,
	0x09,0x02,0x03,0x00,0x04,0x0A,0x00,0x00,0x04,0x0A,0x07,0x00,0x0A,0x04,0x06,0x00,
	0x05,0x09,0x07,0x00,0x04,0x09,0x06,0x00,0x0C,0x0B,0x02,0x00,0x08,0x04,0x06,0x00,
	/* 3D: palette blue component */
	0x00,0x03,0x07,0x04,0x00,0x08,0x00,0x05,0x00,0x04,0x06,0x05,0x02,0x00,0x03,0x03,
	0x00,0x04,0x07,0x04,0x00,0x08,0x03,0x05,0x06,0x04,0x06,0x04,0x00,0x00,0x02,0x00,
	0x06,0x04,0x03,0x03,0x00,0x05,0x03,0x03,0x00,0x0A,0x08,0x05,0x05,0x03,0x03,0x07,
	0x00,0x05,0x06,0x04,0x07,0x03,0x00,0x00,0x02,0x04,0x06,0x09,0x07,0x05,0x03,0x03,
	0x03,0x04,0x07,0x05,0x00,0x08,0x03,0x05,0x03,0x05,0x06,0x05,0x00,0x00,0x00,0x00,
	0x05,0x04,0x09,0x06,0x05,0x04,0x03,0x03,0x00,0x03,0x00,0x00,0x00,0x04,0x00,0x07,
	0x00,0x03,0x00,0x00,0x00,0x04,0x00,0x00,0x00,0x04,0x03,0x07,0x05,0x03,0x00,0x08,
	0x00,0x05,0x03,0x00,0x00,0x06,0x05,0x03,0x00,0x03,0x09,0x06,0x05,0x04,0x03,0x00,
	0x01,0x0A,0x08,0x05,0x00,0x00,0x00,0x08,0x03,0x0F,0x00,0x09,0x07,0x05,0x03,0x00,
	0x01,0x07,0x05,0x03,0x08,0x05,0x03,0x08,0x03,0x05,0x00,0x08,0x06,0x04,0x03,0x00,
	0x01,0x0A,0x07,0x05,0x05,0x04,0x00,0x07,0x04,0x05,0x00,0x03,0x00,0x00,0x00,0x00,
	0x01,0x04,0x00,0x00,0x04,0x00,0x00,0x08,0x04,0x0B,0x04,0x06,0x05,0x03,0x03,0x00,
	0x00,0x0A,0x00,0x00,0x0C,0x00,0x00,0x00,0x0C,0x0A,0x00,0x00,0x0C,0x0B,0x00,0x00,
	0x0C,0x0C,0x00,0x00,0x0C,0x0B,0x00,0x00,0x0C,0x00,0x00,0x00,0x00,0x0C,0x07,0x00,
	0x07,0x00,0x00,0x00,0x00,0x0A,0x00,0x00,0x00,0x0A,0x00,0x00,0x00,0x00,0x00,0x00,
	0x04,0x09,0x06,0x00,0x03,0x07,0x09,0x00,0x0C,0x07,0x06,0x00,0x07,0x03,0x05,0x00
    };



    static MachineDriver machine_driver = new MachineDriver
    (
            /* basic machine hardware */
            new MachineCPU[] {
                    new MachineCPU(
                            CPU_Z80,
                            4000000,	/* 4 Mhz (?) */
                            0,
                            readmem,writemem,null, null,
                            commando_interrupt,2
                    ),
                    new MachineCPU(
                            CPU_Z80 | CPU_AUDIO_CPU,
                            3000000,	/* 3 Mhz ??? */
                            2,	/* memory region #2 */
                            sound_readmem,sound_writemem,null, null,
                            capcom_sh_interrupt,12
                    )
            },
            60,
            null,

            /* video hardware */
            32*8, 32*8, new rectangle( 2*8, 30*8-1, 0*8, 32*8-1 ),
            gfxdecodeinfo,
            256,16*4+4*16+16*8,
            commando_vh_convert_color_prom,
            VIDEO_TYPE_RASTER,
            null,
            commando_vh_start,
            commando_vh_stop,
            commando_vh_screenrefresh,
            
            /* sound hardware */
            null,
            null,
            capcom_sh_start,
            AY8910_sh_stop,
            AY8910_sh_update
    );



    /***************************************************************************

      Game driver(s)

    ***************************************************************************/
    static RomLoadPtr commando_rom= new RomLoadPtr(){ public void handler()  
    {
	ROM_REGION(0x1c000);	/* 64k for code */
	ROM_LOAD( "m09_cm04.bin", 0x0000, 0x8000, 0xf44b9f43 );
	ROM_LOAD( "m08_cm03.bin", 0x8000, 0x4000, 0x6e158a17 );

	ROM_REGION(0x34000);	/* temporary space for graphics (disposed after conversion) */
	ROM_LOAD( "d05_vt01.bin", 0x00000, 0x4000, 0x9c3344b3 );	/* characters */
	ROM_LOAD( "a05_vt11.bin", 0x04000, 0x4000, 0x0babe1d9 );	/* tiles */
	ROM_LOAD( "a06_vt12.bin", 0x08000, 0x4000, 0x0ef15ee7 );
	ROM_LOAD( "a07_vt13.bin", 0x0c000, 0x4000, 0x8244ea38 );
	ROM_LOAD( "a08_vt14.bin", 0x10000, 0x4000, 0x91390ad1 );
	ROM_LOAD( "a09_vt15.bin", 0x14000, 0x4000, 0x755876be );
	ROM_LOAD( "a10_vt16.bin", 0x18000, 0x4000, 0x8c6d8225 );
	ROM_LOAD( "e07_vt05.bin", 0x1c000, 0x4000, 0x4eda8b78 );	/* sprites */
	ROM_LOAD( "e08_vt06.bin", 0x20000, 0x4000, 0x280b34f9 );
	ROM_LOAD( "e09_vt07.bin", 0x24000, 0x4000, 0x2ab5880f );
	ROM_LOAD( "h07_vt08.bin", 0x28000, 0x4000, 0x48696aa7 );
	ROM_LOAD( "h08_vt09.bin", 0x2c000, 0x4000, 0xab521082 );
	ROM_LOAD( "h09_vt10.bin", 0x30000, 0x4000, 0x998c53a6 );

	ROM_REGION(0x10000);	/* 64k for the audio CPU */
	ROM_LOAD( "f09_cm02.bin", 0x0000, 0x4000, 0x07fced60 );
        ROM_END();
    }};
    static RomLoadPtr commandj_rom= new RomLoadPtr(){ public void handler()  
    {
           
	ROM_REGION(0x1c000);	/* 64k for code */
	ROM_LOAD( "09m_so04.bin", 0x0000, 0x8000, 0xf5dffe09 );
	ROM_LOAD( "08m_so03.bin", 0x8000, 0x4000, 0xf8463efe );

	ROM_REGION(0x34000);	/* temporary space for graphics (disposed after conversion) */
	ROM_LOAD( "d05_vt01.bin", 0x00000, 0x4000, 0x9c3344b3 );	/* characters */
	ROM_LOAD( "a05_vt11.bin", 0x04000, 0x4000, 0x0babe1d9 );	/* tiles */
	ROM_LOAD( "a06_vt12.bin", 0x08000, 0x4000, 0x0ef15ee7 );
	ROM_LOAD( "a07_vt13.bin", 0x0c000, 0x4000, 0x8244ea38 );
	ROM_LOAD( "a08_vt14.bin", 0x10000, 0x4000, 0x91390ad1 );
	ROM_LOAD( "a09_vt15.bin", 0x14000, 0x4000, 0x755876be );
	ROM_LOAD( "a10_vt16.bin", 0x18000, 0x4000, 0x8c6d8225 );
	ROM_LOAD( "e07_vt05.bin", 0x1c000, 0x4000, 0x4eda8b78 );	/* sprites */
	ROM_LOAD( "e08_vt06.bin", 0x20000, 0x4000, 0x280b34f9 );
	ROM_LOAD( "e09_vt07.bin", 0x24000, 0x4000, 0x2ab5880f );
	ROM_LOAD( "h07_vt08.bin", 0x28000, 0x4000, 0x48696aa7 );
	ROM_LOAD( "h08_vt09.bin", 0x2c000, 0x4000, 0xab521082 );
	ROM_LOAD( "h09_vt10.bin", 0x30000, 0x4000, 0x998c53a6 );

	ROM_REGION(0x10000);	/* 64k for the audio CPU */
	ROM_LOAD( "09f_so02.bin", 0x0000, 0x4000, 0xfda056a2 );
        ROM_END();
    }};
   

            static  char xortable[] =
            {
		0x00,0x00,0x22,0x22,0x44,0x44,0x66,0x66,0x88,0x88,0xaa,0xaa,0xcc,0xcc,0xee,0xee,
		0x00,0x00,0x22,0x22,0x44,0x44,0x66,0x66,0x88,0x88,0xaa,0xaa,0xcc,0xcc,0xee,0xee,
		0x22,0x22,0x00,0x00,0x66,0x66,0x44,0x44,0xaa,0xaa,0x88,0x88,0xee,0xee,0xcc,0xcc,
		0x22,0x22,0x00,0x00,0x66,0x66,0x44,0x44,0xaa,0xaa,0x88,0x88,0xee,0xee,0xcc,0xcc,
		0x44,0x44,0x66,0x66,0x00,0x00,0x22,0x22,0xcc,0xcc,0xee,0xee,0x88,0x88,0xaa,0xaa,
		0x44,0x44,0x66,0x66,0x00,0x00,0x22,0x22,0xcc,0xcc,0xee,0xee,0x88,0x88,0xaa,0xaa,
		0x66,0x66,0x44,0x44,0x22,0x22,0x00,0x00,0xee,0xee,0xcc,0xcc,0xaa,0xaa,0x88,0x88,
		0x66,0x66,0x44,0x44,0x22,0x22,0x00,0x00,0xee,0xee,0xcc,0xcc,0xaa,0xaa,0x88,0x88,
		0x88,0x88,0xaa,0xaa,0xcc,0xcc,0xee,0xee,0x00,0x00,0x22,0x22,0x44,0x44,0x66,0x66,
		0x88,0x88,0xaa,0xaa,0xcc,0xcc,0xee,0xee,0x00,0x00,0x22,0x22,0x44,0x44,0x66,0x66,
		0xaa,0xaa,0x88,0x88,0xee,0xee,0xcc,0xcc,0x22,0x22,0x00,0x00,0x66,0x66,0x44,0x44,
		0xaa,0xaa,0x88,0x88,0xee,0xee,0xcc,0xcc,0x22,0x22,0x00,0x00,0x66,0x66,0x44,0x44,
		0xcc,0xcc,0xee,0xee,0x88,0x88,0xaa,0xaa,0x44,0x44,0x66,0x66,0x00,0x00,0x22,0x22,
		0xcc,0xcc,0xee,0xee,0x88,0x88,0xaa,0xaa,0x44,0x44,0x66,0x66,0x00,0x00,0x22,0x22,
		0xee,0xee,0xcc,0xcc,0xaa,0xaa,0x88,0x88,0x66,0x66,0x44,0x44,0x22,0x22,0x00,0x00,
		0xee,0xee,0xcc,0xcc,0xaa,0xaa,0x88,0x88,0x66,0x66,0x44,0x44,0x22,0x22,0x00,0x00
            };
    static DecodePtr commando_decode = new DecodePtr()
    {
       public void handler()
       {
           int A;


            for (A = 0;A < 0x10000;A++)
            {
                    if (A > 0) ROM[A] =(char)( RAM[A] ^ xortable[RAM[A]]);
            }
       }
    };

    static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler(String name)
    { 
            /* get RAM pointer (this game is multiCPU, we can't assume the global */
            /* RAM pointer is pointing to the right place) */
            char[] RAM = Machine.memory_region[0];

            /* check if the hi score table has already been initialized */
            if (memcmp(RAM, 0xee00, new char[] { 0x00, 0x50, 0x00}, 3) == 0 &&
                                    memcmp(RAM, 0xe981, new char[] { 0x00, 0x08, 0x00,}, 3) == 0)
            {

                    FILE f;


                    if ((f = fopen(name,"rb")) != null)
                    {
                            fread(RAM,0xee00,1,13*7,f);
                            RAM[0xee97] = RAM[0xee00];
                            RAM[0xee98] = RAM[0xee01];
                            RAM[0xee99] = RAM[0xee02];
                            fclose(f);
                    }

                    return 1;
            }
            else return 0;	/* we can't load the hi scores yet */          
    } };


    static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler(String name){
            FILE f;
            /* get RAM pointer (this game is multiCPU, we can't assume the global */
            /* RAM pointer is pointing to the right place) */
            char[] RAM = Machine.memory_region[0];


            if ((f = fopen(name,"wb")) != null)
            {
                    fwrite(RAM,0xee00,1,13*7,f);
                    fclose(f);
            }
    } };




    public static GameDriver commando_driver =new GameDriver
    (
            "Commando (US version)",
            "commando",
            "PAUL JOHNSON\nNICOLA SALMORIA",
            machine_driver,

            commando_rom,
            null, commando_decode,
            null,

            input_ports,null, trak_ports, dsw, keys,

            color_prom, null, null,
            ORIENTATION_DEFAULT,

            hiload, hisave
    );
    public static GameDriver commandj_driver =new GameDriver
    (
            "Commando (Japanese version)",
            "commandj",
            "PAUL JOHNSON\nNICOLA SALMORIA",
            machine_driver,

            commandj_rom,
            null, commando_decode,
            null,

            input_ports,null, trak_ports, dsw, keys,

            color_prom, null, null,
            ORIENTATION_DEFAULT,

            hiload, hisave
    );
      
}