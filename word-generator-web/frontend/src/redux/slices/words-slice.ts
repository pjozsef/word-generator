import { createSlice } from '@reduxjs/toolkit'
import generateWord from '../async/generate-word'

export type WordsState = {
  current: string[],
  history: string[]
}

const { reducer } = createSlice({
  name: 'words',
  initialState: { current: [], history: [] } as WordsState,
  reducers: {},
  extraReducers: builder => {
    builder.addCase(generateWord.fulfilled, (state, action) => {
      return {
        current: action.payload,
        history: [...state.current, ...state.history]
      }
    })
  }
})

export default reducer
