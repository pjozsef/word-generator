import { createAsyncThunk } from "@reduxjs/toolkit"
import { AppDispatch } from "../store"
import { AppState } from "../app-state"
import axios from 'axios'

export default createAsyncThunk<string[], void, {
    dispatch: AppDispatch
    state: AppState
    extra: object
  }>('words/generate',
    async (_, thunkAPI) => {
      const { getState } = thunkAPI
      const response = await axios.post('/api/generate', {
        expression: getState().command,
        mappings: {}
      })
      return response.data.results
    }
  )