'Generation Configuration
Const ymlExtension As String = ".yml"
Const DataModelSheet = "Data Model"
Const DataSheet = "Inventory"

Const GLB_INDENT As String = "  "
Const DataModelElementName = "Uncertainty Parameters"
Const DataElementName = "uncertaintyGroups"

Sub ExportDataModelToYaml()

    Pathname = Application.ActiveWorkbook.Path
    Filename = Left(Application.ActiveWorkbook.Name, (InStrRev(Application.ActiveWorkbook.Name, ".", -1, vbTextCompare) - 1))
    FilePath = Pathname & "\" & Filename & ymlExtension

    'Create Yaml File
    Call CreateYamlFile(FilePath)

    'Export Data Model to Yaml
    Call ExportParametersToYaml(FilePath, Worksheets(DataModelSheet), DataModelElementName)

    'Export Data to Yaml
    Call ExportDataToYaml(FilePath, Worksheets(DataSheet), DataElementName)
    
End Sub

'Create the Yaml File to Generate
Private Sub CreateYamlFile(FilePath)

    Filenum = FreeFile
    Open FilePath For Output As #Filenum
    Close #Filenum

End Sub

'Export parameters to yml
Private Sub ExportParametersToYaml(FilePath, WorkingSheet, ElementName)
Dim Sheet As Worksheet
    Set PreviousActiveSheet = ActiveSheet
    Set Sheet = WorkingSheet
    
    'Activate sheet to work on it
    Sheet.Activate
        
    levelString = "level"
    requiredString = "required"
    defaultString = "default"
    typeString = "type"
    valuesString = "values"

    'Open the file
    Filenum = FreeFile
    Open FilePath For Append As #Filenum

    'Print the Levels label
    Print #Filenum, ElementName & ":"

    rowOffset = 1
    columnOffset = 1

    Dim columnCount As Long
    columnCount = Sheet.Cells(2, Sheet.Columns.Count).End(xlToLeft).Column - columnOffset
        
        'Parse Field type row
        FieldNameRow = 1 + rowOffset
        FieldLevelRow = FindFieldTypeRow(levelString)
        FieldRequiredRow = FindFieldTypeRow(requiredString)
        FieldDefaultRow = FindFieldTypeRow(defaultString)
        FieldTypeRow = FindFieldTypeRow(typeString)
        FieldValuesRow = FindFieldTypeRow(valuesString)

    'Fields parsing
    For c = 1 + columnOffset To columnCount + columnOffset

        FieldName = ""
        If FieldNameRow > 0 Then
            FieldName = Sheet.Cells(FieldNameRow, c).Value
        End If
        FieldLevel = ""
        If FieldLevelRow > 0 Then
            FieldLevel = Sheet.Cells(FieldLevelRow, c).Value
        End If
        FieldRequired = ""
        If FieldRequiredRow > 0 Then
            FieldRequired = Sheet.Cells(FieldRequiredRow, c).Value
        End If
        FieldDefault = ""
        If FieldDefaultRow > 0 Then
            FieldDefault = Sheet.Cells(FieldDefaultRow, c).Value
        End If
        FieldType = ""
        If FieldTypeRow > 0 Then
            FieldType = Sheet.Cells(FieldTypeRow, c).Value
        End If
        FieldValues = ""
        If FieldValuesRow > 0 Then
            FieldValues = Sheet.Cells(FieldValuesRow, c).Value
        End If

        Print #Filenum, GLB_INDENT & FieldName & ":"
        If FieldLevel <> "" Then
            Print #Filenum, GLB_INDENT & GLB_INDENT & levelString & ": """ & FieldLevel & """"
        End If
        If FieldRequired <> "" Then
            Print #Filenum, GLB_INDENT & GLB_INDENT & requiredString & ": """ & FieldRequired & """"
        End If
        If FieldDefault <> "" Then
            Print #Filenum, GLB_INDENT & GLB_INDENT & defaultString & ": """ & FieldDefault & """"
        End If
        If FieldType <> "" Then
                        Print #Filenum, GLB_INDENT & GLB_INDENT & typeString & ": """ & FieldType & """"
        End If

        If FieldValues <> "" Then
            FieldValuesCount = Sheet.Cells(Sheet.Rows.Count, c).End(xlUp).Row - FieldValuesRow

            If FieldValuesCount > 0 Then
                                                        
                Print #Filenum, GLB_INDENT & GLB_INDENT & valuesString & ":"
                                        
                For optRowOffset = 0 To FieldValuesCount
                                                        
                    FieldValue = Sheet.Cells(FieldValuesRow + optRowOffset, c).Value
                    Print #Filenum, GLB_INDENT & GLB_INDENT & GLB_INDENT & "- """ & FieldValue & """"

                Next optRowOffset
            End If
        End If
    Next c

    Close #Filenum
    
    'Reactivate previous sheet
    PreviousActiveSheet.Activate
    
End Sub

'Find the field type row in column A
Private Function FindFieldTypeRow(FieldType) As Integer
    Dim Cell As Range
    Columns("A:A").Select
    Set Cell = Selection.Find(What:=FieldType, After:=ActiveCell, LookIn:=xlFormulas, _
                    LookAt:=xlWhole, SearchOrder:=xlByRows, SearchDirection:=xlNext, _
                    MatchCase:=False, SearchFormat:=False)
    If Cell Is Nothing Then
    Else
        FindFieldTypeRow = Cell.Row
    End If
        
End Function

'Export Data to yml
Private Sub ExportDataToYaml(FilePath, WorkingSheet, ElementName)
Dim Sheet As Worksheet
    Set PreviousActiveSheet = ActiveSheet
    Set Sheet = WorkingSheet
    
    'Activate sheet to work on it
    Sheet.Activate
    
    nameTag = "name"
    childrenTag = "children"
    valuesTag = "values"
    parameterTag = "parameter"
    valueTag = "value"

    'Open the file
    Filenum = FreeFile
    Open FilePath For Append As #Filenum

    'Print the Levels label
    Print #Filenum, ElementName & ":"

    Dim columnCount As Long
    columnCount = Sheet.Cells(2, Sheet.Columns.Count).End(xlToLeft).Column
    
    Dim firstValueColumn As Long
    firstValueColumn = 2
    
    Dim rowCount As Long
    rowCount = Cells(Rows.Count, firstValueColumn).End(xlUp).Row
        
    Dim rowHeader As Long
    rowHeader = 2

    'Fields parsing
    PreviousLevel = 0
    For Row = rowHeader + 1 To rowCount
    
        Level = Sheet.Cells(Row, firstValueColumn).Value
        
        OffsetSpaces = ""
        For x = 0 To Level
            OffsetSpaces = OffsetSpaces & GLB_INDENT
        Next x
        
        If Level > PreviousLevel Then
            Prefix = OffsetSpaces & childrenTag & ":" & vbNewLine & OffsetSpaces & "- "
        Else
            Prefix = OffsetSpaces & "- "
        End If
        
        First = True
        
        For c = 1 + firstValueColumn To columnCount + columnOffset
            
            Field = Sheet.Cells(rowHeader, c).Value
            Value = Sheet.Cells(Row, c).Value
            
            If Field = nameTag Then
                Print #Filenum, Prefix & Field & ": """ & Value & """"
            Else
                If Value <> "" Then
                    If First Then
                        Print #Filenum, OffsetSpaces & GLB_INDENT & valuesTag & ":"
                        First = False
                    End If
                    Print #Filenum, OffsetSpaces & GLB_INDENT & "- " & parameterTag & ":"
                    Print #Filenum, OffsetSpaces & GLB_INDENT & GLB_INDENT & GLB_INDENT & nameTag & ": """ & Field & """"
                    Print #Filenum, OffsetSpaces & GLB_INDENT & GLB_INDENT & valueTag & ": """ & Value & """"
                End If
            End If
        Next c
        
        PreviousLevel = Level
    Next Row
    
    Close #Filenum
    
    'Reactivate previous sheet
    PreviousActiveSheet.Activate
    
End Sub
