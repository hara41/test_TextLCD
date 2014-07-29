/**
 * 
 */
package jp.hara41;

import jp.nyatla.mimic.mbedjs.MbedJsException;
import jp.nyatla.mimic.mbedjs.javaapi.*;

public class TextLCD{
	private Mcu _mcu;
	private DigitalOut _rs;
	private DigitalOut _e;
	private BusOut _d;
	private LCDType _type;
	private int _column;
	private int _row;
	/**
	 * 
	 * @param mcu
	 * @param rs
	 * @param ee
	 * @param d0
	 * @param d1
	 * @param d2
	 * @param d3
	 * @param type LCD�̋@��
	 */
	public TextLCD(Mcu mcu , int rs, int ee,
	int d0, int d1, int d2, int d3,LCDType type)
	{
		_type = type;
		_mcu = mcu;
		try {
			_rs = new DigitalOut(_mcu , rs);
			_e  = new DigitalOut(_mcu , ee);
			_d  = new BusOut(_mcu , d0, d1, d2, d3);
			
			_e.write(1);
			_rs.write(0);
			
			Thread.sleep(15);
			
			for(int i=0 ; i<3 ; i++){
				writeByte(0x3);
				Thread.sleep(2);
			}
			writeByte(0x2);
			Thread.sleep(1);
			
			writeCommand(0x28);
			writeCommand(0x0C);
			writeCommand(0x6);
			cls();
			
			
			
			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	/**
	 * ������1�����\������
	 * @param c �o�͂��镶��
	 * @return
	 */
	public int putc(int c)
	{
		// ���s����
		if(c == '\n'){
			_column = 0;
			_row ++;
			if(_row >= rows()){
				_row = 0;
			}
		}else{
			character(_column , _row , c);
			_column++;
			if(_column >= columns()){
				_column = 0;
				_row ++ ;
				if(_row >= rows()){
					_row = 0;
				}
			}
		}
		return c;
	}
	/**
	 * �C�ӂ̈ʒu�ɃJ�[�\�����ړ�����
	 * @param column 
	 * @param row
	 */
	public void locate(int column , int row){
		_column = column;
		_row = row;
	}
	/**
	 * �\���������ăJ�[�\�����i0,0�j�Ɉړ�����
	 */
	public void cls()
	{
		writeCommand(0x01);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		locate( 0 , 0);
		
	}
	/**
	 * �s�̐���Ԃ�
	 * @return
	 */
	public int rows()
	{
		switch(_type){
		case LCD20x4:
			return 4;
		case LCD16x2:
		case LCD16x2B:
		case LCD20x2:
			return 2;
		}
		return 0;
	}
	/**
	 * ��̐���Ԃ�
	 * @return
	 */
	public int columns(){
		switch(_type){
		case LCD20x4:
		case LCD20x2:
			return 20;
		case LCD16x2:
		case LCD16x2B:
			return 16;
		}
		return 0;
	}
	/**
	 * �������������ރA�h���X�����肷��
	 * @param column
	 * @param row
	 * @return
	 */
	int address(int column , int row){
		switch (_type){
		case LCD20x4:
			switch(row){
			case 0:
				return 0x80 + column;
			case 1:
				return 0xc0 + column;
			case 2:
				return 0x94 + column;
			case 3:
				return 0xd4 + column;
			}
		case LCD16x2B:
			return 0x80 + (row * 40) +column;
		case LCD16x2:
		case LCD20x2:
			return 0x80+(row * 0x40) +column;
		}
		return 0x80 + (row*0x40) + column;
	}
	/**
	 * �������o�͂���
	 * @param column �s 
	 * @param row ��
	 * @param c ����
	 */
	void character(int column , int row , int c)
	{
		int a = address(column , row);
		writeCommand(a);
		writeData(c);
	}
	/**
	 * 1�o�C�g�o�͂���
	 * @param value
	 */
	void writeByte(int value){
		try {
			_d.write(value >> 4);
			Thread.sleep(1);
			_e.write(0);
			Thread.sleep(1);
			_e.write(1);
			_d.write(value >> 0);
			Thread.sleep(1);
			_e.write(0);
			Thread.sleep(1);
			_e.write(1);
		} catch (MbedJsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * �R�}���h���o��
	 * @param command
	 */
	void writeCommand(int command){
		try {
			_rs.write(0);
		} catch (MbedJsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeByte(command);
	}
	/**
	 * �f�[�^���o��
	 * @param data
	 */
	void writeData(int data){
		try {
			_rs.write(1);
		} catch (MbedJsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeByte(data);
	}
}
