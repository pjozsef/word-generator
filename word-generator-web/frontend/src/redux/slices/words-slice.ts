import { createSlice, PayloadAction, createAsyncThunk } from '@reduxjs/toolkit'
import generateWord from '../async/generate-word'

type ResultState = {
  current: string[],
  history: string[]
}

const { reducer } = createSlice({
  name: 'words',
  initialState: { current: [], history: [] } as ResultState,
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
