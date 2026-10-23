package com.avito.logistics.prompt;

/**
 * Prompts for multimodal extraction using Google Cloud Vertex AI Gemini.
 */
public final class ExtractionPrompts {

    private ExtractionPrompts() {
        // Prevent instantiation
    }

    public static final String PACKING_LIST_EXTRACTION_PROMPT = """
Extract the packing list into the supplied JSON schema. The result will be used to populate a house bill of
lading (HBL), so capture every HBL-relevant value visibly present in the document.

Extract document references when present:
- packing_list_number and packing_list_date
- invoice_number and invoice_date
- exporter_reference, ie_code, branch_code
- buyer_order_number (including values labelled "PO No", "Purchase Order No", or "Buyer Order No")
  and buyer_order_date. Preserve the printed PO number exactly; do not substitute a product/part code.
- every other_references value and shipment_reference
- reference_number exactly as printed on an invoice

Extract parties exactly as printed:
- shipper_exporter, consignee, buyer, and notify_party
- If a field is labelled "Buyer other than Consignee/Notify", extract it as buyer. Set notify_party only when
  the document explicitly identifies a notify party.
- Keep the complete name, address, and tax/registration ID for each party.

For invoices, also extract these spreadsheet values when explicitly printed:
- If a separate Vendor, Supplier, or Consignor block is present (including one shown outside the
  shipper/consignee blocks), capture its complete name, address, and tax identifier in vendor.
  Do not substitute the buyer or consignee for the vendor. When no separate vendor block exists,
  use shipper_exporter only if the invoice identifies it as the supplier/consignor.
- taxable_value, CGST percentage and amount, and SGST percentage and amount for each item line
- invoice-level CGST percentage and amount and SGST percentage and amount when shown as totals
- vehicle_number, shipping_from, and shipping_to
- consignee_gst_number and exporter_gst_number for GST identifiers explicitly identified as GST
  or GSTIN. A vendor/consignor tax number explicitly labelled as its GST number may also be stored
  in exporter_gst_number. Keep a consignor number in vendor.tax_id when it is not identified as GST.
- Keep other party tax, VAT, and registration identifiers in their party tax_id values.
- Do not derive or calculate taxable values, tax rates, or tax amounts.
- Do not copy declarations or terms-and-conditions text into other_particulars.

Extract transport and HBL header details when present:
- country_of_origin and country_of_final_destination
- pre_carriage_by, place_of_receipt, vessel_name, voyage_or_flight_number
- port_of_loading, route_or_place_of_transshipment, port_of_discharge, place_of_final_delivery,
  and type_of_movement
- incoterms, payment_terms, freight_terms, freight_amount, release_terms, delivery_contact,
  other_particulars, place_of_issue, date_of_issue, shipped_on_board_date, and number_of_originals

Extract cargo and package details:
- marks_and_numbers, total_package_count, total_package_type, total_gross_weight, total_net_weight,
  and total_measurement
- For every product or packing line, add one items record. Preserve product/material/part codes,
  product description, crate or package number ranges, index number, HSN code, quantity and unit,
  package count/type, line weights, dimensions/CBM, container number, seal number, and shipping marks.
- Extract hsn_code independently for each item from the HSN/tariff-code value visibly printed for that
  product. Check the full row and adjacent classification heading; an HSN code may be printed in a
  separate heading above the product rows and must still be captured even though that heading is not part
  of item_product_description. For example, when "84149090" is printed with the classification heading
  "PARTS OF AIR OR VACUUM PUMPS & COMPRESSORS - OTHERS" above "Sabre compressor cover-shot blasting",
  capture hsn_code as "84149090" for the item and keep the classification heading out of its description.
  Match a row-specific code only to its corresponding item. If one clearly shared HSN code applies to all
  listed items, use it for each applicable item; do not copy a code between unrelated rows, use a product
  or part number as an HSN, or infer/complete a partially visible code. Leave hsn_code null if its value
  cannot be read or reliably associated with that item.
- If the packing list has a standalone main goods/category description above the item rows (for example,
  "ALUMINIUM ALLOY CAST ARTICLES INCLUDING PARTS & COMPONENTS"), put that exact heading in
  main_item_description. Prefer the specific goods heading immediately above the listed goods, not a
  broader tariff/classification heading or HSN description elsewhere on the page. It applies to the
  listed goods but is not itself an item row. Leave null when no separate heading is printed. Do not use
  column labels, marks, package counts, totals, or tariff/classification descriptions as this heading.
- For skid counts, capture only the number of skids as package_count: in "180 * 4 skids", the count is 4,
  not 180 or 720. For a complete range such as "Skids 1/18 to 18/18", the count is 18. When multiple
  product lines each specify skids, keep each line's count on its own item; the document total is their sum.
- Include only actual goods/products as items. Keep one item record per distinct product line; when multiple
  product names are explicitly listed within one line, separate those names with commas. Do not treat marks,
  package labels, shipping instructions, declarations, or tariff/HSN classification headings as product
  items or as part of item_product_description. For example, "PARTS OF AIR OR VACUUM PUMPS & COMPRESSORS -
  OTHERS " is a classification heading, not a product description; capture the actual product line
  such as "Sabre compressor cover-shot blasting" without that heading.
- When the document prints commercial values for a line, also capture rate as the printed unit price
  and amount as the printed line total/extended value, each including its currency exactly as shown.
  Leave both null on documents that show no price, and never derive one from another figure.
- Capture total_amount from the final grand total or amount payable printed at the end of the invoice,
  after taxes, freight, and other charges when included. Do not use a taxable value, subtotal, or one
  of the line-item amounts as total_amount. Preserve its printed currency and value; use null if no
  final invoice total is visibly stated.
- For every separately listed container, add one containers record with container number, every seal number,
  container size/type, package count/type, gross/net weight, and CBM/measurement.

Rules:
- Use only information visibly present in the document. Do not make up, infer, calculate, or convert values.
- Return null for unknown scalar values and [] for unknown lists.
- Preserve identifiers exactly, including leading zeroes, punctuation, crate ranges, and container check digits.
- Preserve numbers with their printed units. Do not calculate totals or convert units.
- Do not include declarations or terms-and-conditions text as other information.
- Convert a date to YYYY-MM-DD only if its meaning is unambiguous; otherwise retain the source text.
- Keep every product line and every container separate. Do not merge records or omit repeated values.
- Do not treat an invoice number, packing-list number, invoice date, or packing-list date as an HBL/MTD number,
  HBL issue date, shipped-on-board date, freight term, or release term unless the document explicitly says so.
""".strip();
}
