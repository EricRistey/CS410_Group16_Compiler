#include <stdio.h>
#include <stdlib.h>
#include "state_table.h"

int main()
{
	//initial state
	int state = 0;

	//input stream
	char input[1000];
	
	//input stream length
	int input_length = 0;

	//Gather input stream from user
	printf("Enter input stream: \n");
	scanf("%s", &input);

	//print input stream
	printf("Input Stream:\n%s\n", input);

	//States list will contain all final states that the state machine reaches
	int states[1000];

	/* IF WE WANT TO USE DYNAMIC MEMORY
	//Allocate memory for 10 states
	int *states = (int*)malloc(sizeof(int) * 10);
	//Ensure dynamic memory allocation was successful
	if(states == NULL) {
		printf("Memory allocation failed\n");
		exit(1);
	}
	*/

	//current character
	char c;
	//current index
	int i = 0;
	//current token
	char token[10];
	//current token length
	int token_length = 0;

	//read input stream
	while ((c = input[i++]) != '\0')
	{
		//increment input stream length
		input_length++;
		//update state
		state = state_table[state][state_table[state][c]];
		//if state is an acceptable final state
		if (state == final_states[state])
		{
			//store token
			token[token_length] = c;
			//increment token length
			token_length++;
		}

		//store state in states list
		states[token_length - 1] = state;

		//if state is invalid
		if (state == INVALID)
		{
			//reset state
			state = 0;
		}
	}

	//print states
	for (int i = 0; i < token_length; i++)
	{
		printf("%d\n", states[i]);
	}

	return 0;
}