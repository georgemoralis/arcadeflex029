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
 * using automatic conversion tool v0.02
 * converted at : 24-08-2011 22:25:36
 *
 *
 * roms are from v0.36 romset
 *
 */ 
package drivers;

import static arcadeflex.libc.*;
import static mame.commonH.*;
import static mame.cpuintrf.*;
import static mame.driverH.*;
import static mame.mame.*;
import static mame.osdependH.*;
import static sndhrdw.generic.*;
import static sndhrdw.gottlieb.*;
import static machine.gottlieb.*;
import static vidhrdw.generic.*;
import static vidhrdw.gottlieb.*;
import static mame.inptport.*;
import static drivers.qbert.*;
import static mame.memoryH.*;
public class krull
{
		
	static MemoryReadAddress readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x0fff, MRA_RAM ),
		new MemoryReadAddress( 0x1000, 0x2fff, MRA_ROM ),
		new MemoryReadAddress( 0x3000, 0x57ff, MRA_RAM ),
		new MemoryReadAddress( 0x5800, 0x5800, input_port_0_r ),     /* DSW */
		new MemoryReadAddress( 0x5801, 0x5801, krull_IN1_r ),     /* buttons */
		new MemoryReadAddress( 0x5802, 0x5802, input_port_2_r ),     /* trackball: not used */
		new MemoryReadAddress( 0x5803, 0x5803, input_port_3_r ),     /* trackball: not used */
		new MemoryReadAddress( 0x5804, 0x5804, input_port_4_r ),     /* joystick */
		new MemoryReadAddress( 0x6000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x0fff, MWA_RAM ),
		new MemoryWriteAddress( 0x1000, 0x2fff, MWA_ROM ),
		new MemoryWriteAddress( 0x3000, 0x30ff, MWA_RAM, spriteram, spriteram_size ),
		new MemoryWriteAddress( 0x3800, 0x3bff, videoram_w, videoram, videoram_size ),
		new MemoryWriteAddress( 0x4000, 0x4fff, gottlieb_characterram_w, gottlieb_characterram ),
		new MemoryWriteAddress( 0x5000, 0x501f, gottlieb_paletteram_w, gottlieb_paletteram ),
		new MemoryWriteAddress( 0x5800, 0x5800, MWA_RAM ),    /* watchdog timer clear */
		new MemoryWriteAddress( 0x5801, 0x5801, MWA_RAM ),    /* trackball: not used */
		new MemoryWriteAddress( 0x5802, 0x5802, gottlieb_sh_w ), /* sound/speech command */
		new MemoryWriteAddress( 0x5803, 0x5803, gottlieb_output ),       /* OUT1 */
		new MemoryWriteAddress( 0x5804, 0x5804, MWA_RAM ),    /* OUT2 */
		new MemoryWriteAddress( 0x6000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	static MemoryReadAddress krull_sound_readmem[] =
	{
		new MemoryReadAddress( 0x0000, 0x01ff, riot_ram_r ),
		new MemoryReadAddress( 0x0200, 0x03ff, gottlieb_riot_r ),
		new MemoryReadAddress( 0x4000, 0x5fff, gottlieb_sound_expansion_socket_r ),
		new MemoryReadAddress( 0x6000, 0x7fff, MRA_ROM ),
				 /* A15 not decoded except in socket expansion */
		new MemoryReadAddress( 0x8000, 0x81ff, riot_ram_r ),
		new MemoryReadAddress( 0x8200, 0x83ff, gottlieb_riot_r ),
		new MemoryReadAddress( 0xc000, 0xdfff, gottlieb_sound_expansion_socket_r ),
		new MemoryReadAddress( 0xe000, 0xffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	static MemoryWriteAddress krull_sound_writemem[] =
	{
		new MemoryWriteAddress( 0x0000, 0x01ff, riot_ram_w ),
		new MemoryWriteAddress( 0x0200, 0x03ff, gottlieb_riot_w ),
		new MemoryWriteAddress( 0x1000, 0x1000, gottlieb_amplitude_DAC_w ),
		new MemoryWriteAddress( 0x2000, 0x2000, gottlieb_speech_w ),
		new MemoryWriteAddress( 0x3000, 0x3000, gottlieb_speech_clock_DAC_w ),
		new MemoryWriteAddress( 0x4000, 0x5fff, gottlieb_sound_expansion_socket_w ),
		new MemoryWriteAddress( 0x6000, 0x7fff, MWA_ROM ),
				 /* A15 not decoded except in socket expansion */
		new MemoryWriteAddress( 0x8000, 0x81ff, riot_ram_w ),
		new MemoryWriteAddress( 0x8200, 0x83ff, gottlieb_riot_w ),
		new MemoryWriteAddress( 0x9000, 0x9000, gottlieb_amplitude_DAC_w ),
		new MemoryWriteAddress( 0xa000, 0xa000, gottlieb_speech_w ),
		new MemoryWriteAddress( 0xb000, 0xb000, gottlieb_speech_clock_DAC_w ),
		new MemoryWriteAddress( 0xc000, 0xdfff, gottlieb_sound_expansion_socket_w ),
		new MemoryWriteAddress( 0xe000, 0xffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	static InputPort input_ports[] =
	{
		new InputPort(       /* DSW */
			0x00,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* buttons */
			0x01,
			new int[] { 0,                    /* diag mode */
			  OSD_KEY_F2,            /* select */
			  OSD_KEY_3, OSD_KEY_4, /* coin 1 & 2 */
			  0,0,                  /* not connected ? */
			  OSD_KEY_1,
			  OSD_KEY_2 }
		),
		new InputPort(       /* trackball: not used */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* trackball: not used */
			0xff,
			new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }
		),
		new InputPort(       /* joysticks */
			0x00,
			new int[] {
			OSD_KEY_E,OSD_KEY_F,OSD_KEY_D,OSD_KEY_S,
			OSD_KEY_UP,OSD_KEY_RIGHT,OSD_KEY_DOWN,OSD_KEY_LEFT}       /* V.V */
		),
		new InputPort( -1 )  /* end of table */
	};
	
	static TrakPort trak_ports[] =
	{
		new TrakPort( -1 )
	};
	
	static KEYSet keys[] =
	{
		new KEYSet( 4, 4, "MOVE UP" ),
		new KEYSet( 4, 7, "MOVE LEFT"  ),
		new KEYSet( 4, 5, "MOVE RIGHT" ),
		new KEYSet( 4, 6, "MOVE DOWN" ),
		new KEYSet( 4, 0, "FIRE UP" ),
		new KEYSet( 4, 3, "FIRE LEFT"  ),
		new KEYSet( 4, 1, "FIRE RIGHT" ),
		new KEYSet( 4, 2, "FIRE DOWN" ),
		new KEYSet( -1 )
	};
	
	
	static DSW dsw[] =
	{
		new DSW( 0, 0x08, "LIVES PER GAME", new String[] { "3","5" } ),
		new DSW( 0, 0x01, "ATTRACT MODE SOUND", new String[] { "ON", "OFF" } ),
		new DSW( 0, 0x1C, "", new String[] {
			"1 PLAY FOR 1 COIN" , "1 PLAY FOR 2 COINS",
			"1 PLAY FOR 1 COIN" , "1 PLAY FOR 2 COINS",
			"2 PLAYS FOR 1 COIN", "FREE PLAY",
			"2 PLAYS FOR 1 COIN", "FREE PLAY"
			} ),
		new DSW( 0, 0x20, "HEXAGON", new String[] { "ROVING", "STATIONARY" } ),
		new DSW( 0, 0x02, "DIFFICULTY", new String[] { "NORMAL", "HARD" } ),
		new DSW( 0, 0xC0, "", new String[] {
			"LIFE AT 30000 THEN EVERY 50000",
			"LIFE AT 30000 THEN EVERY 30000",
			"LIFE AT 40000 THEN EVERY 50000",
			"LIFE AT 50000 THEN EVERY 75000"
			} ),
		/*new DSW( 1, 0x01, "TEST MODE", new String[] {"ON", "OFF"} ),*/
		new DSW( -1 )
	};
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,    /* 8*8 characters */
		256,    /* 256 characters */
		4,      /* 4 bits per pixel */
		new int[] { 0, 1, 2, 3 },
		new int[] { 0, 4, 8, 12, 16, 20, 24, 28},
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8    /* every char takes 32 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,  /* 16*16 sprites */
		256,    /* 256 sprites */
		4,      /* 4 bits per pixel */
		new int[] { 0, 0x2000*8, 0x4000*8, 0x6000*8 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*8    /* every sprite takes 32 consecutive bytes */
	);
	
	
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( 0, 0x4000, charlayout, 0, 1 ),       /* the game dynamically modifies this */
		new GfxDecodeInfo( 1, 0, spritelayout, 0, 1 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static MachineDriver machine_driver = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_I86,
				5000000,        /* 5 Mhz */
				0,
				readmem,writemem,null,null,
				nmi_interrupt,1
			),
			new MachineCPU(
				CPU_M6502 | CPU_AUDIO_CPU ,
				3579545/4,        /* could it be /2 ? */
				2,             /* memory region #2 */
				krull_sound_readmem,krull_sound_writemem,null,null,
				gottlieb_sh_interrupt,1
			)
	
		},
		60,     /* frames / second */
		null,      /* init machine */
	
		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 0*8, 30*8-1 ),
		gfxdecodeinfo,
		1+16, 16,
		gottlieb_vh_init_color_palette,
	
		VIDEO_TYPE_RASTER|VIDEO_SUPPORTS_DIRTY|VIDEO_MODIFIES_PALETTE,
		null,      /* init vh */
		krull_vh_start,
		generic_vh_stop,
		gottlieb_vh_screenrefresh,
	
		/* sound hardware */
		null,      /* samples */
		null,
		gottlieb_sh_start,
		gottlieb_sh_stop,
		gottlieb_sh_update
	);
	
	
	
	static RomLoadPtr krull_rom = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000);    /* 64k for code */
		ROM_LOAD( "RAM2.BIN", 0x1000, 0x1000, 0x03fa87a8 );
		ROM_LOAD( "RAM4.BIN", 0x2000, 0x1000, 0x8d50227a );
		ROM_LOAD( "ROM4.BIN", 0x6000, 0x2000, 0x5e10647c );
		ROM_LOAD( "ROM3.BIN", 0x8000, 0x2000, 0xdda2011c );
		ROM_LOAD( "ROM2.BIN", 0xa000, 0x2000, 0x2ab22372 );
		ROM_LOAD( "ROM1.BIN", 0xc000, 0x2000, 0x5341023f );
		ROM_LOAD( "ROM0.BIN", 0xe000, 0x2000, 0x16e7bc1d );
	
		ROM_REGION(0x8000);     /* temporary space for graphics */
		ROM_LOAD( "FG3.BIN", 0x0000, 0x2000, 0xf7bee74c );      /* sprites */
		ROM_LOAD( "FG2.BIN", 0x2000, 0x2000, 0xcf79bc05 );      /* sprites */
		ROM_LOAD( "FG1.BIN", 0x4000, 0x2000, 0xf2f27094 );      /* sprites */
		ROM_LOAD( "FG0.BIN", 0x6000, 0x2000, 0xdae82e5a );      /* sprites */
	
		ROM_REGION(0x10000);     /* 64k for sound cpu */
		ROM_LOAD( "snd1.bin", 0xe000, 0x1000, 0x7390800c );
			ROM_RELOAD(0x6000, 0x1000);/* A15 is not decoded */
		ROM_LOAD( "snd2.bin", 0xf000, 0x1000, 0xe65ea116 );
			ROM_RELOAD(0x7000, 0x1000);/* A15 is not decoded */
	
	ROM_END(); }}; 
	
	
	
	static HiscoreLoadPtr hiload = new HiscoreLoadPtr() { public int handler() 
	{
		/* check if the hi score table has already been initialized */
	/*TOFIX	if (memcmp(RAM,0x0b3d,new char[] { 0x7F,0x7F,0x7F,0x00,0x00,0x00,0x00,0x00,0x00,0x00 },10) == 0 &&
				memcmp(RAM,0x0c2d,new char[] { 0x7F,0x7F,0x7F,0x00,0x00,0x00,0x00,0x00,0x00,0x00 },10) == 0)
		{
			FILE f;
	
	
			if ((f = fopen(name,"rb")) != null)
			{
				fread(RAM,0x0ace,10*10,1,f);
				fread(RAM,0x0b3d,10*25,1,f);
				fclose(f);
			}
	
			return 1;
		}
		else */return 0;  /* we can't load the hi scores yet */
	} };
	
	
	
	static HiscoreSavePtr hisave = new HiscoreSavePtr() { public void handler() 
	{
		FILE f;
	
	
	/*TOFIX	if ((f = fopen(name,"wb")) != null)
		{
			fwrite(RAM,0x0ace,10*10,1,f);
			fwrite(RAM,0x0b3d,10*25,1,f);
			fclose(f);
		}*/
	} };
	
	
	
	public static GameDriver krull_driver = new GameDriver
	(
		"Krull",
		"krull",
		"FABRICE FRANCES",
		machine_driver,
	
		krull_rom,
		null, null,   /* rom decode and opcode decode functions */
		gottlieb_sample_names,
	
		input_ports, null, trak_ports, dsw, keys,
	
		null, null, null,
		ORIENTATION_ROTATE_270,
	
		hiload, hisave
	);
}
