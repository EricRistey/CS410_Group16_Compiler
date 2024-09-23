
#ifndef STATE_TABLE_H
#define STATE_TABLE_H

//states encoding
#define INVALID 999
#define START 0
#define OP_PAREN 1
#define CL_PAREN 2
#define OP_BRACK 3
#define CL_BRACK 4
#define OP_BRACE 5
#define CL_BRACE 6
#define IDENTIFIER 7
#define F 8
#define FO 9
#define FOR 10
#define I 11
#define IN 12
#define INT 13
#define IF 14

//acceptable final states
extern const int final_states[10];

//limited dictionary for encoding characters (may not be necessary in final implementation)
/*
characters = {" ": 0, "(": 1, ")": 2, "[": 3,
              "]": 4, "{": 5, "}": 6,
              "f": 7, "o": 8, "r": 9,
               "i": 10, "n": 11, "t": 12  }
*/

//state table
extern int state_table[14][13];

#endif
 

