import { createAsyncThunk } from "@reduxjs/toolkit"
import { AppDispatch } from "../store"
import { AppState } from "../app-state"
import axios from 'axios'

const removeEmptyLines = (mappings: object) => {
  return Object.entries(mappings).map(([key, value]) => {
    const filtered = (value as string[]).map((v)=>v.trim()).filter((v) => v !== '')
    return { [key]: filtered }
  }).reduce((curr, acc) => ({ ...acc, ...curr }), {})
}

export default createAsyncThunk<string[], void, {
  dispatch: AppDispatch
  state: AppState
  extra: object
}>('words/generate',
  async (_, thunkAPI) => {
    const { getState } = thunkAPI
    const { command, categories: { all } } = getState()
    const mappings = all.reduce((acc, curr) => {
      const e = { [curr.name]: curr.value.split('\n') }
      return { ...acc, ...e }
    }, {})
    const response = await axios.post('/api/generate', {
      expression: command,
      mappings: removeEmptyLines(mappings),
      times: 15
    })
    return response.data.results
  }
)
