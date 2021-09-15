'Generation Configuration
Const ymlExtension As String = ".yml"
Const GLB_INDENT As String = "  "
Const ElementName = "Decisions"

Sub ExportDataModelToYaml()

    Pathname = Application.ActiveWorkbook.Path
    Filename = Left(Application.ActiveWorkbook.Name, (InStrRev(Application.ActiveWorkbook.Name, ".", -1, vbTextCompare) - 1))
    FilePath = Pathname & "\" & Filename & ymlExtension

    'Create Yaml File
    Call CreateYamlFile(FilePath)

    'Export Excel Sheets to Yaml
    Call ExportParametersToYaml(FilePath, ActiveSheet, ElementName)

    'Call ExportContentToYaml
End Sub

'Create the Yaml File to Generate
Private Sub CreateYamlFile(FilePath)

    Filenum = FreeFile
    Open FilePath For Output As #Filenum
    Close #Filenum

End Sub

'Export parameters to yml
Private Sub ExportParametersToYaml(FilePath, DataModelSheet, ElementName)
Dim Sheet As Worksheet
    Set Sheet = DataModelSheet
    
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


