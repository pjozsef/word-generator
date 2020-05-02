import {createSlice, PayloadAction } from '@reduxjs/toolkit'

type ResultState = {
  result: string[],
  history: string[]
}

const {actions, reducer} = createSlice({
    name: 'result',
    initialState: {result: [], history: []} as ResultState,
    reducers: {
      newResult(state, action: PayloadAction<string[]>) {
        return {
          result: action.payload,
          history: [...state.result, ...state.history]
        }
      }
    }
  })

  export const { newResult } = actions

  export default reducer