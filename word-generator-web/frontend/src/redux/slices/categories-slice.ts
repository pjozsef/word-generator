import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import _ from 'lodash'

export type CategoryActions = {
  addCategory: (name: string) => void
  updateCategory: (category:{name: string, value: string}) => void
  deleteCategory: (name: string) => void
}

export type CategoriesState = {
  [category: string]: string
}

const { actions, reducer } = createSlice({
  name: 'categories',
  initialState: {} as CategoriesState,
  reducers: {
    addCategory(state, action: PayloadAction<string>) {
      return {
        [action.payload]: '',
        ...state
      }
    },
    updateCategory(state, action: PayloadAction<{name: string, value: string}>) {
      const {name, value } = action.payload
      return {
        ...state,
        [name]: value
      }
    },
    deleteCategory(state, action: PayloadAction<string>) {
      return _.omit(state, [action.payload])
    },
  }
})

export const { addCategory, updateCategory, deleteCategory } = actions

export default reducer